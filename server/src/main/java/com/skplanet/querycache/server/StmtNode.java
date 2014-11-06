package com.skplanet.querycache.server;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.util.RuntimeProfile.QueryProfile;

public class StmtNode {
  private static final Logger LOG = LoggerFactory.getLogger(StmtNode.class);
  long sStmtId = 0;
  ConnNode conn = null;
  Statement sHStmt = null;
  boolean sHasResultSet = false;
  ResultSet sRS = null;
  QueryProfile profile = null;
  RowFetcher rowProducer = null;
  
  public static enum State {
    INIT,
    EXEC,
    GETMETA,
    FETCH,
    CLOSE,
    ERROR
  }
  
  public void initialize(ConnNode conn, long aStmtId, Connection aConn) throws SQLException {
    this.conn = conn;
    this.sStmtId = aStmtId;
    this.sHStmt = aConn.createStatement();
    this.sHasResultSet = false;
    this.sRS = null;
    this.profile = new QueryProfile();
    profile.stmtState = State.INIT;
  }
  
  synchronized public RowFetcher allocRowProducer(String queryId,
    CLIHandler cliHandler) {
    if (rowProducer == null){
      rowProducer = new RowFetcher(queryId, this, cliHandler);
    }
    return rowProducer;
  }
}
