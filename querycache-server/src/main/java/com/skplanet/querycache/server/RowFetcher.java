package com.skplanet.querycache.server;

import com.skplanet.querycache.server.StmtNode.State;
import com.skplanet.querycache.server.util.ObjectPool;
import com.skplanet.querycache.thrift.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RowFetcher implements Runnable {
  private static final boolean DEBUG = false;
  private static final Logger LOG = LoggerFactory.getLogger(RowFetcher.class);
  private static final int _maxRowsInQueue = 20480; // ~20MB when row is 1KB big

  private long _fetchSize = Long.MAX_VALUE;
  private final int _logLvl;
  private CLIHandler _cliHandler;
  private ObjectPool _objPool;

  private final StmtNode _stmt;
  private final String _queryId;
  private final AtomicLong _rowCount = new AtomicLong(0);
  private final AtomicBoolean _stop = new AtomicBoolean(false);
  private final AtomicBoolean _hasMoreRows = new AtomicBoolean(true);
  private final AtomicBoolean _isDone = new AtomicBoolean(false);

  // Queue for fetched rows getting from the Database
  private final ArrayDeque<TRow> _rowQ = new ArrayDeque<>(_maxRowsInQueue);
  private final ArrayDeque<TRow> _rowQToBeRecycled = new ArrayDeque<>(_maxRowsInQueue);
  private final List<Object> _rowSetToBeRecycled = new ArrayList<Object>();

  private ResultSet sRS = null;
  private ResultSetMetaData sMeta = null;
  private int sColCnt = 0;

  private final Object fetcherMutex = new Object();

  RowFetcher(String queryId, StmtNode stmt, CLIHandler cliHandler) {
    this._queryId = queryId;
    this._stmt = stmt;
    this._cliHandler = cliHandler;
    this._objPool = _cliHandler.getObjPool();
    this._logLvl = _cliHandler.profileLvl;
  }

  private TRow getRowFromResultSet() throws SQLException {
    TRow sRow = (TRow)_objPool.getObject(ObjectPool.POOL_TROW);
    for (int i = 1; i <= sColCnt; i++) {
      TColumnValue sCell = (TColumnValue)_objPool.getObject(ObjectPool.POOL_TCOLUMNVALUE);
      TTypeId sQCType = _cliHandler.mapSQL2QCType(sMeta.getColumnType(i));
      switch (sQCType) {
        case CHAR:
        case STRING:
        case BINARY:
        case DECIMAL:
          String value = sRS.getString(i);
          TStringValue sStrV = (TStringValue)_objPool.getObject(ObjectPool.POOL_TSTRINGVALUE);
          sCell.setStringVal(sStrV.setValue(value));
          break;
        case BIGINT:
          sCell.setI64Val(sRS.getLong(i));
          break;
        case TIMESTAMP:
          // TIMESTAMP -> long
          Timestamp sTS = sRS.getTimestamp(i);
          sCell.setI64Val(sTS.getTime());
          break;
        case BOOLEAN:
          sCell.setBoolVal(sRS.getBoolean(i));
          break;
        case DOUBLE:
        case FLOAT:
          sCell.setDoubleVal(sRS.getDouble(i));
          break;
        case INT:
          sCell.setI32Val(sRS.getInt(i));
          break;
        case SMALLINT:
          sCell.setI16Val(sRS.getShort(i));
          break;
        case TINYINT:
          sCell.setByteVal(sRS.getByte(i));
          break;
        case DATE:
        case UNSUPPORTED:
        case UNKNOWN:
        default:
          sCell.clear();
          break;
      }
      sRow.addToColVals(sCell);
    }
    return sRow;
  }

  private boolean addRowInQueue(TRow row) {
    if (_rowQ.size() >= _maxRowsInQueue ) {
      boolean bExitWithError = false;
      if (!_stop.get()) {
        synchronized (fetcherMutex) {
          try {
            fetcherMutex.wait();
          } catch (InterruptedException e) {
            bExitWithError = true;
          }
        }
      }
      if (_stop.get() || bExitWithError) {
        // free the row and return false;
        _objPool.recycleObject(row);
        return false;
      }
    }
    synchronized(_rowQ) {
      _rowQ.add(row);
      if (_rowQ.size() >= _fetchSize) {
        _rowQ.notifyAll();
      }
    }
    return true;
  }

  public void run() {
    LOG.debug("Producer fetchResults is running.");
    long startTime = 0;
    long endTime, midTime;
    //1-0=sumGetConn,2-1=sumGetStmt,3=endGetStmt,4-3=initFetcher,5-4=getMeta,6-5=1stFetch,
    //7=sumNext,8=sumGetCol,9=lastNext,10=EndTS
    long timeArr[] = _stmt.profile.fetchProfile;
    int  j = 4, k;
    
    if (_stmt.sHasResultSet != true) {
      LOG.error("Producer fetchResults error : The statement has no ResultSet." +
        " (id:" + _queryId + ")");
      CLIHandler.gConnMgrs.runtimeProfile.moveRunToCompleteProfileMap(
        _queryId, State.ERROR);
      return;
    }
    
    if (_logLvl > 0) {
      timeArr[j++] = startTime = System.currentTimeMillis(); //index-3
    }
    
    //
    // 1. Fetch each row and save to TRowSet
    //
    try {
      sRS = _stmt.sRS;
      sMeta = sRS.getMetaData();
      sColCnt = sMeta.getColumnCount();

      if (_logLvl > 1)
        timeArr[j++] = System.currentTimeMillis(); // T0; index-4
      
      // TODO: Below code block should be more optimized for performance
      k = j;
      while (sRS.next() && !_stop.get()) {
        // TODO: time-logging code below is wrong!!
        // why add System.currentTimeMillis() again and again?
        if (_logLvl > 2) {
          midTime = System.currentTimeMillis(); // T1
          if (_rowCount.get() == 1) {
            // put first-fetch TS
            timeArr[j++] = midTime; // 1st fetch; index-5
            k = j;
          }
          j = k;
          // put the sum of T1 to calculate sum time of next()
          timeArr[j++] += midTime; // index-6
        }

        _rowCount.incrementAndGet();
        TRow sRow = getRowFromResultSet();

        if (_logLvl > 2) {
          midTime = System.currentTimeMillis(); // T2
          // put the sum of T2 to calculate sum time of get cols
          timeArr[j++] += midTime; // index-7
          // we also need TS of the last T2
          timeArr[j++] = midTime; // lastNext; index-8
        }

        if (!addRowInQueue(sRow)) {
          // stopped or interrupted
          close();
          break;
        }
      }

      // close result set
      sRS.close();
      _isDone.set(true);

      if (!_stop.get()) {
        // if not forcibly stopped transferring, notify last rows left in queue
        synchronized(_rowQ) {
          _rowQ.notifyAll();
        }
      }

      _stmt.profile.rowCnt = _rowCount.get();
      if (_logLvl > 2) {
        if (_rowCount.get() > 0) {
          // calculate sum time of next and get cols
          // sum of next() = (sum from i=2 to n of TS1 - sum from i=1 to n-1 of TS2)
          // sum of get cols = (sum from i=1 to n of TS3 - sum from i=1 to n of TS2)
          midTime = timeArr[k];
          timeArr[k] =
            ((timeArr[k]-timeArr[k-1])-(timeArr[k+1]-timeArr[k+2]));
          timeArr[k+1] = (timeArr[k+1] - midTime);
        }
      }
      
    } catch (SQLException e) {
      LOG.error("Producer fetchResults error (" + e.getSQLState() + ") :" + e.getMessage(), e);
      close();

      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        CLIHandler.gConnMgrs.removeConn(_stmt.conn.sConnType, _stmt.conn.sConnId);
        LOG.warn("FetchResults: Removing a failed connection (connId:" + _stmt.conn.sConnId + ")");
      }
      CLIHandler.gConnMgrs.runtimeProfile.moveRunToCompleteProfileMap(
        _queryId, State.ERROR);
    }

    if (_logLvl > 0) {
      endTime = System.currentTimeMillis(); // T3
      LOG.debug("Producer fetchResults PROFILE: QueryId={}, Type={}, RowCnt={}, Fetch time elapsed={}ms",
              _queryId, _stmt.conn.sConnType, _rowCount.get(), endTime - startTime);
      _stmt.profile.timeHistogram[2] = endTime-startTime;
      if (_logLvl > 1) {
        if (_logLvl > 2 && _rowCount.get() == 0) {
          timeArr[j++] = endTime;
          timeArr[j++] = timeArr[j++] = 0;
          timeArr[j++] = endTime;
        }
        timeArr[j++] = endTime; // index-9
      }
    }
  }
  
  public void close() {
    _stop.set(true);
    synchronized (fetcherMutex) {
      fetcherMutex.notifyAll();
    }
    synchronized (_rowQ) {
      _objPool.recycleRows(_rowQ);
      _rowQ.notifyAll();
    }
    synchronized (_rowSetToBeRecycled) {
      _objPool.recycleObjects(_rowSetToBeRecycled, ObjectPool.POOL_TROWSET);
    }
    queueRowsToBeRecycled(null);
  }
  
  public long getRows(List<TRow> dest, long fetchSize) {
    long rowCount = 0;
    synchronized (_rowQ) {
      if (_fetchSize == Long.MAX_VALUE)
        _fetchSize = fetchSize;
      while (true) {
        if (_stop.get()) {
          break;
        }

        if (_isDone.get()) {
          // fetched all rows from backend
          rowCount = getRowsFromQueue(dest, fetchSize);
          if (_rowQ.size()==0) {
            _hasMoreRows.set(false);
          }
          break;
        }

        // getRows while rowFetcher is getting row from backend
        if (_rowQ.size() < fetchSize) {
          try {
            _rowQ.wait();
          } catch (InterruptedException e) {
            LOG.error("Producer getRows: " + e.getMessage(), e);
          }
          // continue looping
        } else {
          rowCount = getRowsFromQueue(dest, fetchSize);
          synchronized (fetcherMutex) {
            fetcherMutex.notifyAll();
          }
          break;
        }
      }
    }
    return rowCount;
  }
  
  // must be called within synchronized(_rowQ) block
  private long getRowsFromQueue(List<TRow> dest, long rowCount) {
    long count;
    count = Math.min( rowCount, _rowQ.size() );
    for (int i=0; i<count; i++) {
      dest.add(_rowQ.remove());
    }
    return count;
  }

  public void queueRowsToBeRecycled(List<TRow> rowList) {
    synchronized (_rowQToBeRecycled) {
      _objPool.recycleRows(_rowQToBeRecycled);
      if (rowList != null) {
        _rowQToBeRecycled.addAll(rowList);
      }
    }
  }

  // for object recycling.
  // in close(), TRowSet instances will be recycled by ObjectPool
  public void queueRowSetToBeRecycled(TRowSet rowSet) {
    synchronized (_rowSetToBeRecycled) {
      _rowSetToBeRecycled.add(rowSet);
    }
  }

  public boolean hasMoreRows() {
    return _hasMoreRows.get();
  }
}
