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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RowFetcher implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(RowFetcher.class);
  private static int _logLvl = QueryCacheServer.conf.getInt(QCConfigKeys.QC_QUERY_PROFILING_LEVEL,
    QCConfigKeys.QC_QUERY_PROFILING_LEVEL_DEFAULT);
  private static CLIHandler _cliHandler;
  private static ObjectPool _objPool;
  //private Thread _t;
  private StmtNode _stmt;
  private AtomicLong _cursoroffset = new AtomicLong(0);
  private AtomicLong _fetchedoffset = new AtomicLong(0);
  private AtomicBoolean _stop = new AtomicBoolean(false);
  public  AtomicBoolean _hasMoreRows = new AtomicBoolean(true);
  // Queue for fetched rows getting from the Database
  private List<TRow> _rowQ = new ArrayList<TRow>(1024);
  String _queryId;
  public AtomicBoolean _isFetching = new AtomicBoolean(false);
  public AtomicBoolean _isDone = new AtomicBoolean(false);
  //
  private long _fetchSize = Long.MAX_VALUE;
  private List<Object> _rowsetUsed = new ArrayList<Object>();
  
  RowFetcher(String queryId, StmtNode stmt, CLIHandler cliHandler) {
    this._queryId = queryId;
    this._stmt = stmt;
    RowFetcher._cliHandler = cliHandler;
    RowFetcher._objPool = _cliHandler.getObjPool();
    RowFetcher._logLvl = _cliHandler.profileLvl;
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
      CLIHandler.gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
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
      ResultSet sRS = _stmt.sRS;
      ResultSetMetaData sMeta = sRS.getMetaData();

      int sColCnt = sMeta.getColumnCount();
      if (_logLvl > 1)
        timeArr[j++] = System.currentTimeMillis(); // T0; index-4
      
      // TODO: Below code block should be more optimized for performance
      k = j;
      while (sRS.next()) {
        _fetchedoffset.incrementAndGet();
        TRow sRow = (TRow)_objPool.getObject(ObjectPool.POOL_TROW);
        if (_logLvl > 2) {
          midTime = System.currentTimeMillis(); // T1
          if (_fetchedoffset.get() == 1) {
            // put first-fetch TS
            timeArr[j++] = midTime; // 1st fetch; index-5
            k = j;
          }
          j = k;
          // put the sum of T1 to calculate sum time of next()
          timeArr[j++] += midTime; // index-6
        }
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
        _rowQ.add(sRow);
        if (_logLvl > 2) {
          midTime = System.currentTimeMillis(); // T2
          // put the sum of T2 to calculate sum time of get cols
          timeArr[j++] += midTime; // index-7
          // we also need TS of the last T2
          timeArr[j++] = midTime; // lastNext; index-8
        }
        if ((_rowQ.size() - _cursoroffset.get()) >= _fetchSize) {
          synchronized(_rowQ) {
            _rowQ.notifyAll();
          }
        }
        if (_stop.get()) {
          _objPool.recycleRows(_rowQ);
          sRS.close();
          break;
        }
      }
      if (_stop.get() != true) {
        synchronized(_rowQ) {
          _isDone.set(true);
          _rowQ.notifyAll();
        }
      }
      _stmt.profile.rowCnt = _fetchedoffset.get();
      if (_logLvl > 2) {
        if (_fetchedoffset.get() > 0) {
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
      _stop.set(true);
      _objPool.recycleRows(_rowQ);
      synchronized(_rowQ) {
        _rowQ.notifyAll();
      }
      if (e.getSQLState().equals("08S01")) {
        // remove failed ConnNode in the ConnPool
        CLIHandler.gConnMgr.removeConn(_stmt.conn.sConnType, _stmt.conn.sConnId);
        LOG.warn("FetchResults: Removing a failed connection (connId:" + _stmt.conn.sConnId + ")");
      }
      CLIHandler.gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        _queryId, State.ERROR);
    }
    if (_logLvl > 0) {
      endTime = System.currentTimeMillis(); // T3
      LOG.info("Producer fetchResults PROFILE: QueryId=" + _queryId + ", Type="
        + _stmt.conn.sConnType + ", RowCnt=" + _fetchedoffset.get() + ", Fetch time elapsed="
        + (endTime-startTime) + "ms");
      _stmt.profile.timeHistogram[2] = endTime-startTime;
      if (_logLvl > 1) {
        if (_logLvl > 2 && _fetchedoffset.get() == 0) {
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
    _objPool.recycleRows(_rowQ);
    _objPool.recycleObjects(_rowsetUsed, ObjectPool.POOL_TROWSET);
  }
  
  public List<TRow> getRows(long fetchSize) {
    List<TRow> result = null;
    int position = 0;
    boolean escape = false;
    boolean fetching, done;

    synchronized (_rowQ) {
      if (_fetchSize == Long.MAX_VALUE)
        _fetchSize = fetchSize;
      while (escape == false) {
        fetching = _isFetching.get();
        done = _isDone.get();
        if (_stop.get()) {
          result = null;
          escape = true;
        } else if (fetching && !done) { // currently fetching rows
          if ((_rowQ.size() - _cursoroffset.get()) < fetchSize) {
            try {
              _rowQ.wait();
            } catch (InterruptedException e) {
              LOG.error("Producer getRows: " + e.getMessage(), e);
            }
          } else {
            position = (int) _cursoroffset.get();
            // shallow copy
            result = subRowQ(position, position + (int) fetchSize);
            _cursoroffset.addAndGet(fetchSize);
            escape = true;
          }
        } else if (fetching && done) { // have fetched all rows 
          if ((_rowQ.size() - _cursoroffset.get()) < fetchSize) {
            position = (int) _cursoroffset.get();
            result = subRowQ(position, (int) _rowQ.size());
            _cursoroffset.addAndGet(_rowQ.size() - _cursoroffset.get());
            escape = true;
          } else {
            position = (int) _cursoroffset.get();
            result = subRowQ(position, position + (int) fetchSize);
            _cursoroffset.addAndGet(fetchSize);
            escape = true;
          }
          if (_fetchedoffset.get() == _cursoroffset.get())
            _hasMoreRows.set(false);
        } else {
          // never happen
          LOG.error("Consumer call getRows in the wrong state.");
          result = null;
          escape = true;
        }
      }
    }
    return result;
  }
  
  private List<TRow> subRowQ(int from, int to) {
    ArrayList<TRow> dest = new ArrayList<TRow>(to - from);
    // shallow copy
    for (int i = from; i < to; i++) {
      dest.add(_rowQ.get(i));
    }
    return dest;
  }

  // for object recycling.
  // in close(), TRowSet instances will be recycled by ObjectPool
  public void addRowSet(TRowSet rowset) {
    _rowsetUsed.add(rowset);
  }
}
