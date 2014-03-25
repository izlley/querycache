package com.skplanet.querycache.server;

import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.common.Types.ConnType;

public class ConnNode {
  private static final Logger LOG = LoggerFactory.getLogger(ConnNode.class);
  
  long sConnId = 0;
  Connection sHConn = null;
  ConnType sConnType;
  State sState;
  
  //TODO : use hashcode instead of long
  private final AtomicLong sStmtIdGen = new AtomicLong(0L);
  ConcurrentHashMap<Long, StmtNode> sStmtMap =
      new ConcurrentHashMap<Long, StmtNode>(16, 0.9f, 1);
  
  public static enum State {
    CONNECTED,
    CLOSED,
    ERROR
  }
  
  public void initialize(ConnType aConnType, long aId, String aUrl) 
      throws SQLException, LinkageError, ClassNotFoundException {
    Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
    this.sConnId = aId;
    this.sHConn = DriverManager.getConnection(aUrl);
    this.sState = State.CONNECTED;
    this.sConnType = aConnType;
  }
  
  public void finalize() {
    this.sConnId = 0;
    this.sHConn = null;
    this.sState = State.CLOSED;
    this.sStmtMap.clear();
  }
  
  public StmtNode getStmt(long aStmtId) {
    // return null if this map contains no mapping for the key
    StmtNode sStmt = sStmtMap.get(aStmtId);
    if (sStmt == null) {
      LOG.warn("There is no statement in StmtPool mapping to the id" + "(" +
        aStmtId + ")");
    }
    return sStmt;
  }
  
  public StmtNode allocStmt() throws SQLException {
    long sId = sStmtIdGen.addAndGet(1L);
    while (sStmtMap.containsKey(sId) == true) {
      sId = sStmtIdGen.addAndGet(1L);
    }
    StmtNode sStmt = new StmtNode();
    sStmt.initialize(sId, this.sHConn);
    if (sStmtMap.put(sId, sStmt) != null) {
      LOG.warn("There is same statement id in StmtPool" + "(" + sId + ")");
    }
    LOG.info("The statement is added.-ConnId:" + this.sConnId + ",StmtId:" +
        sId + ",-# of Stmts:" + sStmtMap.size());
    return sStmt;
  }
  
  public void closeStmt(long aStmtId) throws SQLException {
    StmtNode sStmt = sStmtMap.remove(aStmtId);  // O(1)
    LOG.info("The statement is closed.-ConnId:" + this.sConnId + ",StmtId:" +
        aStmtId + ",-# of Stmts:" + sStmtMap.size());
    if (sStmt != null) {
      sStmt.sHStmt.close();
    } else {
      LOG.debug("The statement is already closed." + "(" +
          aStmtId + ")");
    }
  }
  
  public void closeAllStmts() throws SQLException {
    LOG.debug("Closing all statements in ConNode Id:" + sConnId);

    Iterator<ConcurrentHashMap.Entry<Long, StmtNode>> iterator =
      sStmtMap.entrySet().iterator();

    // http://java67.blogspot.kr/2013/08/best-way-to-iterate-over-each-entry-in.html
    // O(n)
    while (iterator.hasNext()) {
      ConcurrentHashMap.Entry<Long, StmtNode> sEntry = iterator.next();
      sEntry.getValue().sHStmt.close();
      iterator.remove();
    }
    LOG.info("All statements are closed.-ConnId:" + this.sConnId + ",-# of Stmts:" +
        sStmtMap.size());
  }
}
