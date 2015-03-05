package com.skplanet.querycache.server.util;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.QCConfigKeys;
import com.skplanet.querycache.server.QueryCacheServer;
import com.skplanet.querycache.thrift.*;

// TODO: to minimize GC, object recycle logic needs to be implemented
// TODO: find re-entrant queue or linked list, to minimize use of synchronized() {} block
//       This looks very promising: http://concurrencykit.org/, except that the kit is written in C

public class ObjectPool {
  private static final boolean DEBUG = false;
  private static final Logger LOG = LoggerFactory.getLogger(ObjectPool.class);

  public static final int POOL_TROWSET = 0;
  public static final int POOL_TROW = 1;
  public static final int POOL_TCOLUMNVALUE = 2;
  public static final int POOL_TSTRINGVALUE = 3;
  public static final int POOLS_COUNT = 4; // number of object pools

  // Object pool
  private List<ArrayDeque<Object>> sObjPools;

  private final int profileLvl = QueryCacheServer.conf.getInt(QCConfigKeys.QC_QUERY_PROFILING_LEVEL,
      QCConfigKeys.QC_QUERY_PROFILING_LEVEL_DEFAULT);

  private long sReloadCycle = 0;
  private int  sMaxPoolSize = 0;
  private float sReloadingThreshold = 0.f;
  private int sCellCoeff = 0;
  
  private Runnable sReloadThread = new Runnable() {
    public void run() {
      LOG.info("ObjectPool reloading thread init.");
      boolean interrupted = false;
      try {
        while (true) {
          try {
            Thread.sleep(sReloadCycle);
            LOG.debug("ObjectPool: checking each object count.");
            for (int i = 0; i < POOLS_COUNT; i++) {
              LOG.debug("  ObjList[{}].size()={}", i, sObjPools.get(i).size());
              if (i == POOL_TCOLUMNVALUE || i == POOL_TSTRINGVALUE) {
                if (sObjPools.get(i).size() < (int)((sMaxPoolSize*sCellCoeff) *
                    sReloadingThreshold)) {
                  LOG.info("Reloading more objects in the ObjectPool[{}].size()={}",
                          i, sObjPools.get(i).size());
                  initObjPool(i);
                }
              } else {
                if (sObjPools.get(i).size() < (int)(sMaxPoolSize * sReloadingThreshold)) {
                  LOG.info("Reloading more objects in the ObjectPool[{}].size()={}",
                          i, sObjPools.get(i).size());
                  initObjPool(i);
                }
              }
            }
          } catch (InterruptedException e) {
            // Deliberately ignore
            interrupted = true;
          }
        }
      } finally {
        if (interrupted) {
          Thread.currentThread().interrupt();
        }
      }
    }
  };
  
  public ObjectPool(int aPoolSize, int aCellCoeff, long aReloadCycle, float aReloadF) {
    sMaxPoolSize = aPoolSize;
    sCellCoeff   = aCellCoeff;
    sReloadCycle = aReloadCycle;
    sReloadingThreshold = aReloadF;

    // append,remove : O(1). ArrayDeque<> is slightly better than LinkedList<>
    sObjPools = new ArrayList<ArrayDeque<Object>>(POOLS_COUNT);
    sObjPools.add(POOL_TROWSET, new ArrayDeque<Object>(sMaxPoolSize));
    initObjPool(POOL_TROWSET);
    sObjPools.add(POOL_TROW, new ArrayDeque<Object>(sMaxPoolSize));
    initObjPool(POOL_TROW);
    sObjPools.add(POOL_TCOLUMNVALUE, new ArrayDeque<Object>(sMaxPoolSize * sCellCoeff));
    initObjPool(POOL_TCOLUMNVALUE);
    sObjPools.add(POOL_TSTRINGVALUE, new ArrayDeque<Object>(sMaxPoolSize * sCellCoeff));
    initObjPool(POOL_TSTRINGVALUE);

//    new Thread(sReloadThread).start();
  }

  private void initObjPool(int aInd) {
    LOG.info("Supplying obj[" + aInd + "] to the ObjectPool...");
    ArrayDeque<Object> pool = sObjPools.get(aInd);
    synchronized(pool) {
      switch (aInd) {
        case POOL_TROWSET:
          for (int i = pool.size(); i < sMaxPoolSize; i++) {
            // append : O(1)
            pool.add(new TRowSet());
          }
          break;
        case POOL_TROW:
          for (int i = pool.size(); i < sMaxPoolSize; i++) {
            pool.add(new TRow());
          }
          break;
        case POOL_TCOLUMNVALUE:
          // we need lots of Cell objects, so approximatively multiply by 2.
          for (int i = pool.size(); i < (sMaxPoolSize * sCellCoeff); i++) {
            pool.add(new TColumnValue());
          }
          break;
        case POOL_TSTRINGVALUE:
          for (int i = pool.size(); i < (sMaxPoolSize * sCellCoeff); i++) {
            pool.add(new TStringValue());
          }
        default:
          break;
      }
    }
  }
  
  public Object getObject(int aInd) {
    Object sObj;
    try {
      ArrayDeque<Object> pool = sObjPools.get(aInd);
      synchronized(pool) {
        sObj = pool.remove();
      }
    } catch (NoSuchElementException e) {
      sObj = null;
    }

    if (sObj == null) {
      // no object ready for specified type
      // allocate one and return it.
      switch (aInd) {
        case POOL_TROWSET:
          sObj = new TRowSet();
          break;
        case POOL_TROW:
          sObj = new TRow();
          break;
        case POOL_TCOLUMNVALUE:
          sObj = new TColumnValue();
          break;
        case POOL_TSTRINGVALUE:
          sObj = new TStringValue();
          break;
        default:
          LOG.error("Wrong pool specified.");
          break;
      }
    }
    return sObj;
  }

  // recycle single object, avoid use of this if possible.
  public void recycleObject(Object obj) {
    ArrayDeque<Object> pool = null;
    if (obj instanceof TRowSet) {
      pool = sObjPools.get(POOL_TROWSET);
    }
    else if (obj instanceof TRow) {
      pool = sObjPools.get(POOL_TROW);
    }
    else if (obj instanceof TColumnValue) {
      pool = sObjPools.get(POOL_TCOLUMNVALUE);
    }
    else if (obj instanceof TStringValue) {
      pool = sObjPools.get(POOL_TSTRINGVALUE);
    }

    if (pool != null) {
      synchronized (pool) {
        pool.add(obj);
      }
    }
  }

  // recycle object collection of single type.
  // poolDesc: specify which pool the objects belongs to.
  public void recycleObjects(Collection<Object> objs, int poolDesc) {
    ArrayDeque<Object> pool = null;
    switch (poolDesc) {
      case POOL_TROWSET:
        pool = sObjPools.get(POOL_TROWSET);
        break;
      case POOL_TROW:
        pool = sObjPools.get(POOL_TROW);
        break;
      case POOL_TCOLUMNVALUE:
        pool = sObjPools.get(POOL_TCOLUMNVALUE);
        break;
      case POOL_TSTRINGVALUE:
        pool = sObjPools.get(POOL_TSTRINGVALUE);
        break;
    }

    if (pool != null) {
      synchronized (pool) {
        pool.addAll(objs);
      }
    }

    objs.clear();
  }

  public void recycleRows(Collection<TRow> rows) {
    // to minimize use of synchronized {} block, use local collections
    ArrayDeque<Object> lRows = new ArrayDeque<Object>(64);
    ArrayDeque<Object> lCols = new ArrayDeque<Object>(256);
    ArrayDeque<Object> lStrs = new ArrayDeque<Object>(256);

    for (TRow row: rows) {
      List<TColumnValue> cols = row.getColVals();
      row.clear();
      for (TColumnValue col: cols) {
        Object obj = col.getFieldValue();
        col.clear();
        if (obj instanceof TStringValue) {
          TStringValue str = (TStringValue)obj;
          str.clear();
          lStrs.add(str);
        }
        lCols.add(col);
      }
      cols.clear();
      lRows.add(row);
    }
    rows.clear();

    LOG.debug("recycling {} rows, {} cols, {} strs.",
            lRows.size(), lCols.size(), lStrs.size());

    recycleObjects(lRows, POOL_TROW);
    recycleObjects(lCols, POOL_TCOLUMNVALUE);
    recycleObjects(lStrs, POOL_TSTRINGVALUE);
  }

  public class Profile {
    // configuration
    public long sReloadCycle;
    public int  sMaxPoolSize;
    public float sReloadingThreshold;
    public int sCellCoeff;

    public int[] poolSize;

    public Profile() {
      this.sReloadCycle = ObjectPool.this.sReloadCycle;
      this.sMaxPoolSize = ObjectPool.this.sMaxPoolSize;
      this.sReloadingThreshold = ObjectPool.this.sReloadingThreshold;
      this.sCellCoeff = ObjectPool.this.sCellCoeff;

      this.poolSize = new int[POOLS_COUNT];
      poolSize[POOL_TROWSET] = ObjectPool.this.sObjPools.get(POOL_TROWSET).size();
      poolSize[POOL_TROW] = ObjectPool.this.sObjPools.get(POOL_TROW).size();
      poolSize[POOL_TCOLUMNVALUE] = ObjectPool.this.sObjPools.get(POOL_TCOLUMNVALUE).size();
      poolSize[POOL_TSTRINGVALUE] = ObjectPool.this.sObjPools.get(POOL_TSTRINGVALUE).size();
    }
  }
}