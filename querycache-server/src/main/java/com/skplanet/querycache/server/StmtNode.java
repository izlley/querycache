package com.skplanet.querycache.server;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.util.RuntimeProfile.QueryProfile;

public class StmtNode {
  private static final boolean DEBUG = false;
  private static final Logger LOG = LoggerFactory.getLogger(StmtNode.class);
  public final long sStmtId;
  ConnNode conn = null;
  Statement sHStmt = null;
  boolean sHasResultSet = false;
  ResultSet sRS = null;
  QueryProfile profile = null;
  RowFetcher rowProducer = null;
  boolean isCanceled = false;
  
  public static enum State {
    INIT,
    EXEC,
    EXEC_COMPLETE,
    GETMETA,
    GETSCHEMAS,
    GETCATALOGS,
    GETTABLES,
    GETTABLETYPES,
    GETCOLUMNS,
    GETTYPEINFO,
    GETFUNCTIONS,
    FETCH,
    CLOSE,
    CANCEL,
    ERROR
  }

  public StmtNode(ConnNode conn, long aStmtId, Connection aConn, boolean getstmt)
    throws SQLException {
    this.conn = conn;
    this.sStmtId = aStmtId;
    if (getstmt)
      this.sHStmt = aConn.createStatement();
    this.sHasResultSet = false;
    this.sRS = null;
  }
  
  synchronized public RowFetcher allocRowProducer(String queryId,
    CLIHandler cliHandler) {
    if (rowProducer == null){
      rowProducer = new RowFetcher(queryId, this, cliHandler);
    }
    return rowProducer;
  }
}
