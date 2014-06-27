package com.skplanet.querycache.server;

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StmtNode {
  private static final Logger LOG = LoggerFactory.getLogger(StmtNode.class);
  
  //for debug
  long sumLatency = 0;
  
  long sStmtId = 0;
  Statement sHStmt = null;
  boolean sHasResultSet = false;
  ResultSet sRS = null;
  String sQuery = "";
  long rowCnt = 0;
  State sState = State.CLOSED;
  
  public static enum State {
    INIT,
    RUNNING,
    FETCHING,
    CLOSED,
    ERROR
  }
  
  public void initialize(long aStmtId, Connection aConn) throws SQLException {
    this.sStmtId = aStmtId;
    this.sHStmt = aConn.createStatement();
    this.sState = State.INIT;
    this.sHasResultSet = false;
    this.sRS = null;
    this.sQuery = "";
    this.rowCnt = 0;
  }
}
