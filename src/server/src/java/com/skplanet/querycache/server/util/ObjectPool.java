package com.skplanet.querycache.server.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.QCConfigKeys;
import com.skplanet.querycache.server.QueryCacheServer;
import com.skplanet.querycache.thrift.*;

public class ObjectPool {
  private static final Logger LOG = LoggerFactory.getLogger(ObjectPool.class);
  private final int profileLvl = QueryCacheServer.conf.getInt(QCConfigKeys.QC_QUERY_PROFILING_LEVEL,
      QCConfigKeys.QC_QUERY_PROFILING_LEVEL_DEFAULT);
  
  // Object pool
  // TODO: preventing a full GC we need a logic for recycling object
  List<List<Object>> sObjList = new ArrayList<List<Object>>();
  
  private long sReloadCycle = 0;
  private int  sMaxPoolSize = 0;
  private float sReloadingThreshhold = 0.f;
  private int sCellCoeff = 0;
  
  private int  sObjCnt = 4;
  public static class TargetObjs {
    public static final int TROWSET = 0;
    public static final int TROW = 1;
    public static final int TCOLUMNVALUE = 2;
    public static final int TSTRINGVALUE = 3;
    // If you have a new object for pooling, add here
  }
  
  private Runnable sReloadThread = new Runnable() {
    public void run() {
      LOG.info("ObjectPool reloading thread init.");
      boolean interrupted = false;
      try {
        while (true) {
          try {
            Thread.sleep(sReloadCycle);
            LOG.debug("ObjectPool: checking each object count.");
            for (int i = 0; i < sObjCnt; i++) {
              LOG.debug("  ObjList[" + i + "].size()=" + sObjList.get(i).size());
              if (i == TargetObjs.TCOLUMNVALUE || i == TargetObjs.TSTRINGVALUE) {
                if (sObjList.get(i).size() < (int)((sMaxPoolSize*sCellCoeff) *
                    sReloadingThreshhold)) {
                  LOG.info("Reloading more objects in the ObjectPool[" + i + "]"
                      + ".size()=" + sObjList.get(i).size());
                  initObjPool(i);
                }
              } else {
                if (sObjList.get(i).size() < (int)(sMaxPoolSize * sReloadingThreshhold)) {
                  LOG.info("Reloading more objects in the ObjectPool[" + i + "]"
                      + ".size()=" + sObjList.get(i).size());
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
    sReloadingThreshhold = aReloadF;
    for (int i = 0; i < sObjCnt; i++) {
      // create a new obj-linkedlist and add to the sObjList
      // append : O(1)
      sObjList.add(new LinkedList<Object>());
      initObjPool(i);
    }
    
    new Thread(sReloadThread).start();
  }
  
  private void initObjPool(int aInd) {
    LOG.info("Supplying obj[" + aInd + "] to the ObjectPool...");
    switch (aInd) {
      case 0:
        for (int i = sObjList.get(aInd).size(); i < sMaxPoolSize; i++) {
          // append : O(1)
          sObjList.get(aInd).add((Object)new TRowSet());
        }
        break;
      case 1:
        for (int i = sObjList.get(aInd).size(); i < sMaxPoolSize; i++) {
          sObjList.get(aInd).add((Object)new TRow());
        }
        break;
      case 2:
        // we need lots of Cell objects, so approximatively multiply by 2.
        for (int i = sObjList.get(aInd).size(); 
             i < (sMaxPoolSize * sCellCoeff); i++) {
          sObjList.get(aInd).add((Object)new TColumnValue());
        }
        break;
      case 3:
        for (int i = sObjList.get(aInd).size(); 
            i < (sMaxPoolSize * sCellCoeff); i++) {
         sObjList.get(aInd).add((Object)new TStringValue());
       }
      default:
        break;
    }
  }
  
  public Object getObject(int aInd) {
    try {
      // get(ind) : O(1)
      // remove first element : O(1)
      List<Object> sObjs = sObjList.get(aInd);
      Object sObj = null;
      // TODO: Is there any lock-free linked-list?
      //       This looks very promising: http://concurrencykit.org/
      synchronized(sObjs) {
        sObj = sObjs.remove(0);
      }
      return sObj;
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }
}