package com.skplanet.querycache.server;

import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.cli.thrift.THostInfo;
import com.skplanet.querycache.cli.thrift.TStatusCode;

public class ConnNode {
  private static final Logger LOG = LoggerFactory.getLogger(ConnNode.class);
  
  // for debug 
  // 0:open/1:connclose
  long[] latency = {-1,-1};
  
  long sConnId = 0;
  Connection sHConn = null;
  String sConnType;
  private State  sState;
  private String url;
  
  // for checking authentication&authorization
  String user = null;
  private String password = null;
  THostInfo clientInfo = null;
  
  //TODO : use hashcode instead of long
  private final AtomicLong sStmtIdGen = new AtomicLong(0L);
  ConcurrentHashMap<Long, StmtNode> sStmtMap =
      new ConcurrentHashMap<Long, StmtNode>(16, 0.9f, 1);
  
  public static enum State {
    CONNECTED,
    CLOSED,
    ERROR
  }
  
  public void initialize(ConnProperty aConnType,
                         long aId,
                         String aUrl) 
      throws SQLException, LinkageError, ClassNotFoundException {
    Class.forName(aConnType.connPkgPath);
    this.sConnId = aId;
    if (aConnType.connUserId == null) {
      this.sHConn = DriverManager.getConnection(aUrl);
    } else {
      this.sHConn = DriverManager.getConnection(aUrl,
        aConnType.connUserId, aConnType.connPass);
    }
    this.sState = State.CONNECTED;
    this.sConnType = aConnType.connTypeName;
    this.url = aUrl;
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
          sConnType + ":" + aStmtId + ")");
    }
    return sStmt;
  }
  
  public StmtNode allocStmt(boolean getstmt) throws SQLException {
    long sId = sStmtIdGen.addAndGet(1L);
    while (sStmtMap.containsKey(sId) == true) {
      sId = sStmtIdGen.addAndGet(1L);
    }
    StmtNode sStmt = new StmtNode();
    sStmt.initialize(this, sId, this.sHConn, getstmt);
    // setting query profile
    sStmt.profile.queryId = sConnId + ":" + sId;
    sStmt.profile.user = this.user;
    sStmt.profile.startTime = System.currentTimeMillis();
    
    if (sStmtMap.put(sId, sStmt) != null) {
      LOG.warn("There is same statement id in StmtPool" + "(" + sConnType + 
        ":" + sId + ")");
    }
    LOG.info("The statement is added.-Type:" + sConnType + ", -ConnId:" + 
      this.sConnId + ", StmtId:" + sId + ", -# of Stmts:" + sStmtMap.size());
    
    if (CLIHandler.gConnMgr.runtimeProfile.addRunningQuery(sStmt.profile.queryId, sStmt.profile)
        != null) {
      LOG.warn("There is same query-id in RunningProfileMap " + "(queryId:" + 
        sStmt.profile.queryId + ")");
    }
    return sStmt;
  }
  
  public StmtNode closeStmt(long aStmtId) throws SQLException {
    StmtNode sStmt = sStmtMap.remove(aStmtId);  // O(1)
    if (sStmt != null) {
      if (sStmt.isCanceled) {
        throw new SQLException("Canceled", "HY000", TStatusCode.ERROR_STATUS.getValue());
      }
      if (sStmt.rowProducer != null)
        sStmt.rowProducer.close();
      sStmt.sHStmt.close();
      LOG.info("The statement is closed.-Type:" + sConnType + ", -ConnId:" +
        this.sConnId + ",StmtId:" + aStmtId + ",-# of Stmts:" + sStmtMap.size());
    } else {
      LOG.debug("The statement is already closed." + "(" +
          sConnType + ":" + aStmtId + ")");
    }
    return sStmt;
  }
  
  public void closeAllStmts() throws SQLException {
    LOG.debug("Closing all statements in ConNode Id -" + sConnType +
        ":" + sConnId);
    String sQueryId;

    Iterator<ConcurrentHashMap.Entry<Long, StmtNode>> iterator =
      sStmtMap.entrySet().iterator();

    // http://java67.blogspot.kr/2013/08/best-way-to-iterate-over-each-entry-in.html
    // O(n)
    while (iterator.hasNext()) {
      ConcurrentHashMap.Entry<Long, StmtNode> sEntry = iterator.next();
      StmtNode stmtNode = sEntry.getValue();
      if (stmtNode.sHStmt != null && !stmtNode.isCanceled) {
        stmtNode.sHStmt.close();
        // move QueryProfile to completeQueryProfile Map
        sQueryId = this.sConnId + ":" + sEntry.getValue().sStmtId;
        CLIHandler.gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
          sQueryId, StmtNode.State.CLOSE);
      }
      iterator.remove();
    }
    LOG.info("All statements are closed.-Type:" + sConnType + ", -ConnId:"
        + this.sConnId + ",-# of Stmts:" + sStmtMap.size());
  }
  
  public StmtNode cancelStmt(long aStmtId) throws SQLException {
    StmtNode sStmt = sStmtMap.get(aStmtId);  // O(1)
    if (sStmt != null) {
      if (sStmt.isCanceled) {
        throw new SQLException("Canceled", "HY000", TStatusCode.ERROR_STATUS.getValue());
      }

      if (sStmt.rowProducer != null)
        sStmt.rowProducer.close();
      sStmt.isCanceled = true;
      try {
        sStmt.sHStmt.cancel();
      } catch (SQLException e) {
        LOG.error("exception while cancelling a query", e);
      }

      LOG.info("The statement is canceled.-Type:" + sConnType + ", -ConnId:" +
        this.sConnId + ",StmtId:" + aStmtId);
    } else {
      LOG.debug("The statement is already closed." + "(" +
          sConnType + ":" + aStmtId + ")");
    }
    return sStmt;
  }
  
  public void setPassword(String aPass) {
    this.password = aPass;
  }
  
  public String getPassword() {
    return password;
  }
  
  public String getUrl() {
    return url;
  }
}
