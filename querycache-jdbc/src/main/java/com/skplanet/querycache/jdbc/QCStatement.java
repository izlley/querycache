package com.skplanet.querycache.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.skplanet.querycache.cli.thrift.*;

/**
 * QCStatement.
 *
 */
public class QCStatement implements java.sql.Statement {
  private TCLIService.Iface client;
  private TOperationHandle stmtHandle;
  private final TSessionHandle sessHandle;
  private final Map<String,String> sessConf = new HashMap<String,String>();
  private int fetchSize = 1024;
  private boolean isScrollableResultset = false;
  private ReentrantLock transportLock = new ReentrantLock(true);
  /**
   * We need to keep a reference to the result set to support the following:
   * <code>
   * statement.execute(String sql);
   * statement.getResultSet();
   * </code>.
   */
  private ResultSet resultSet = null;

  /**
   * Sets the limit for the maximum number of rows that any ResultSet object produced by this
   * Statement can contain to the given number. If the limit is exceeded, the excess rows
   * are silently dropped. The value must be >= 0, and 0 means there is not limit.
   */
  private int maxRows = 0;

  /**
   * Add SQLWarnings to the warningChain if needed.
   */
  private SQLWarning warningChain = null;

  /**
   * Keep state so we can fail certain calls made after close().
   */
  private boolean isClosed = false;

  /**
   *
   */
  public QCStatement(TCLIService.Iface client, TSessionHandle sessHandle) {
    this(client, sessHandle, false);
  }

  public QCStatement(TCLIService.Iface client, TSessionHandle sessHandle,
        boolean isScrollableResultset) {
    this.client = client;
    this.sessHandle = sessHandle;
    this.isScrollableResultset = isScrollableResultset;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#addBatch(java.lang.String)
   */

  public void addBatch(String sql) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#cancel()
   */

  public void cancel() throws SQLException {
    if (isClosed || stmtHandle == null) {
      throw new SQLException("Can't cancel after statement has been closed", "24000");
    }

    TCancelOperationReq cancelReq = new TCancelOperationReq();
    cancelReq.setOperationHandle(stmtHandle);
    try {
      transportLock.lock();
      TCancelOperationResp cancelResp = client.CancelOperation(cancelReq);
      Utils.verifySuccessWithInfo(cancelResp.getStatus());
    } catch (Exception e) {
      throw new SQLException(e.toString(), "08S01", e);
    } finally {
      transportLock.unlock();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#clearBatch()
   */

  public void clearBatch() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#clearWarnings()
   */

  public void clearWarnings() throws SQLException {
    warningChain = null;
  }

  private void closeClientOperation() throws SQLException {
    try {
      clearWarnings();
      if (stmtHandle != null) {
        TCloseOperationReq closeReq = new TCloseOperationReq();
        closeReq.setOperationHandle(stmtHandle);
        transportLock.lock();
        TCloseOperationResp closeResp = client.CloseOperation(closeReq);
      }
    } catch (SQLException e) {
      throw e;
    } catch (Exception e) {
      throw new SQLException(e.getMessage(), "08S01", e);
    } finally {
      stmtHandle = null;
      transportLock.unlock();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#close()
   */

  public void close() throws SQLException {
    if (isClosed) {
      return;
    }
    closeClientOperation();
    client = null;
    resultSet = null;
    isClosed = true;
  }

  public void closeOnCompletion() throws SQLException {
    // JDK 1.7
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#execute(java.lang.String)
   */

  public boolean execute(String sql) throws SQLException {
    if (isClosed) {
      throw new SQLException("Can't execute after statement has been closed");
    }

      //closeClientOperation();
    TExecuteStatementReq execReq = new TExecuteStatementReq(sessHandle, sql);
    TExecuteStatementResp execResp;
    execReq.setConfiguration(sessConf);
    execReq.setAsyncMode(true);
    transportLock.lock();
    try {
      execResp = client.ExecuteStatement(execReq);
    } catch (Exception ex) {
      throw new SQLException(ex.toString(), "08S01", ex);
    } finally {
      transportLock.unlock();
    }

    if (execResp.status.getStatusCode() != TStatusCode.SUCCESS_STATUS) {
      throw new SQLException(execResp.status.getErrorMessage(), execResp.status.getSqlState(), execResp.status.getErrorCode());
    }

    /* Hmm..
    if (execResp.getStatus().getStatusCode().equals(TStatusCode.STILL_EXECUTING_STATUS)) {
      warningChain = Utils.addWarning(warningChain, new SQLWarning("Query execuing asynchronously"));
    } else {
      Utils.verifySuccessWithInfo(execResp.getStatus());
    }
    */

    // polling getOperationStatus()
    stmtHandle = execResp.getOperationHandle();
    boolean keepPolling = true;
    TGetOperationStatusReq osReq = new TGetOperationStatusReq(stmtHandle);
    while (keepPolling) {
      TGetOperationStatusResp osResp;
      transportLock.lock();
      try {
        osResp = client.GetOperationStatus(osReq);
      } catch (Exception ex) {
        throw new SQLException(ex.toString(), "08S01", ex);
      } finally {
        transportLock.unlock();
      }

      if (osResp.status.statusCode != TStatusCode.SUCCESS_STATUS) {
        throw new SQLException(osResp.status.getErrorMessage(), osResp.status.getSqlState(), osResp.status.getErrorCode());

      }
      switch (osResp.operationState) {
        case INITIALIZED_STATE:
        case RUNNING_STATE:
          // still running
          break;
        case FINISHED_STATE:
          keepPolling = false;
          stmtHandle = osResp.getOperationHandle();
          break;
        case CANCELED_STATE:
        case ERROR_STATE:
        case CLOSED_STATE: /* never returns this state from serve-side yet */
        case UNKNOWN_STATE: /* never returns this state from serve-side yet */
          throw new SQLException(osResp.status.getErrorMessage(), osResp.status.getSqlState(), osResp.status.getErrorCode());
      }
    }

    if (!stmtHandle.isHasResultSet()) {
      return false;
    }
    resultSet =  new QCQueryResultSet.Builder().setClient(client).setSessionHandle(sessHandle)
        .setStmtHandle(stmtHandle).setMaxRows(maxRows).setFetchSize(fetchSize)
        .setScrollable(isScrollableResultset)
        .build();
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#execute(java.lang.String, int)
   */

  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#execute(java.lang.String, int[])
   */

  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
   */

  public boolean execute(String sql, String[] columnNames) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#executeBatch()
   */

  public int[] executeBatch() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#executeQuery(java.lang.String)
   */

  public ResultSet executeQuery(String sql) throws SQLException {
    if (!execute(sql)) {
      throw new SQLException("The query did not generate a result set!");
    }
    return resultSet;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#executeUpdate(java.lang.String)
   */

  public int executeUpdate(String sql) throws SQLException {
    execute(sql);
    return 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#executeUpdate(java.lang.String, int)
   */

  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
   */

  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public String getLog() throws SQLException {
    throw new SQLException("Method not supported");
    /*
    if (isClosed) {
      throw new SQLException("Can't get log for statement after statement has been closed");
    }

    TGetLogReq getLogReq = new TGetLogReq();
    TGetLogResp getLogResp;
    getLogReq.setOperationHandle(stmtHandle);
    try {
      getLogResp = client.GetLog(getLogReq);
      Utils.verifySuccessWithInfo(getLogResp.getStatus());
    } catch (SQLException e) {
      throw e;
    } catch (Exception e) {
      throw new SQLException(e.toString(), "08S01", e);
    }
    return getLogResp.getLog();
    */
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
   */

  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getConnection()
   */

  public Connection getConnection() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getFetchDirection()
   */

  public int getFetchDirection() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getFetchSize()
   */

  public int getFetchSize() throws SQLException {
    return fetchSize;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getGeneratedKeys()
   */

  public ResultSet getGeneratedKeys() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getMaxFieldSize()
   */

  public int getMaxFieldSize() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getMaxRows()
   */

  public int getMaxRows() throws SQLException {
    return maxRows;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getMoreResults()
   */

  public boolean getMoreResults() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getMoreResults(int)
   */

  public boolean getMoreResults(int current) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getQueryTimeout()
   */

  public int getQueryTimeout() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getResultSet()
   */

  public ResultSet getResultSet() throws SQLException {
    return resultSet;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getResultSetConcurrency()
   */

  public int getResultSetConcurrency() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getResultSetHoldability()
   */

  public int getResultSetHoldability() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getResultSetType()
   */

  public int getResultSetType() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getUpdateCount()
   */

  public int getUpdateCount() throws SQLException {
    return 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#getWarnings()
   */

  public SQLWarning getWarnings() throws SQLException {
    return warningChain;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#isClosed()
   */

  public boolean isClosed() throws SQLException {
    return isClosed;
  }

  public boolean isCloseOnCompletion() throws SQLException {
    // JDK 1.7
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#isPoolable()
   */

  public boolean isPoolable() throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#setCursorName(java.lang.String)
   */

  public void setCursorName(String name) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#setEscapeProcessing(boolean)
   */

  public void setEscapeProcessing(boolean enable) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#setFetchDirection(int)
   */

  public void setFetchDirection(int direction) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#setFetchSize(int)
   */

  public void setFetchSize(int rows) throws SQLException {
    fetchSize = rows;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#setMaxFieldSize(int)
   */

  public void setMaxFieldSize(int max) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#setMaxRows(int)
   */

  public void setMaxRows(int max) throws SQLException {
    if (max < 0) {
      throw new SQLException("max must be >= 0");
    }
    maxRows = max;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#setPoolable(boolean)
   */

  public void setPoolable(boolean poolable) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Statement#setQueryTimeout(int)
   */

  public void setQueryTimeout(int seconds) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
   */

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   *
   * @see java.sql.Wrapper#unwrap(java.lang.Class)
   */

  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException("Method not supported");
  }

}
