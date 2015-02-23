/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.skplanet.querycache.server;

import com.skplanet.querycache.cli.thrift.*;
import com.skplanet.querycache.server.StmtNode.State;
import com.skplanet.querycache.server.common.AnalyzerException;
import com.skplanet.querycache.server.common.InternalType.CORE_RESULT;
import com.skplanet.querycache.server.sqlcompiler.Analyzer;
import com.skplanet.querycache.server.sqlcompiler.AuthorizationException;
import com.skplanet.querycache.server.util.ObjectPool;
import com.skplanet.querycache.thrift.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// Generated code

public class CLIHandler implements TCLIService.Iface {
  private static final Logger LOG = LoggerFactory.getLogger(CLIHandler.class);
  private static final int _defFetchSize = 1024;
  public final int profileLvl = QueryCacheServer.conf.getInt(QCConfigKeys.QC_QUERY_PROFILING_LEVEL,
    QCConfigKeys.QC_QUERY_PROFILING_LEVEL_DEFAULT);
  private final boolean isSyntaxCheck =
    QueryCacheServer.conf.getBoolean(QCConfigKeys.QC_QUERY_SYNTAX_CHECK,
      QCConfigKeys.QC_QUERY_SYNTAX_CHECK_DEFAULT);

  private static ExecutorService _threadPool = new ThreadPoolExecutor(
    QueryCacheServer.conf.getInt(QCConfigKeys.QC_THREADPOOL_INIT_SIZE,
      QCConfigKeys.QC_THREADPOOL_INIT_SIZE_DEFAULT),
    QueryCacheServer.conf.getInt(QCConfigKeys.QC_THREADPOOL_MAX_SIZE,
      QCConfigKeys.QC_THREADPOOL_MAX_SIZE_DEFAULT),
    60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
  public static ConnMgr gConnMgr = new ConnMgr();
  
  private static ObjectPool gObjPool =
    new ObjectPool(
      QueryCacheServer.conf.getInt(QCConfigKeys.QC_OBJECTPOOL_MAX_SIZE,
        QCConfigKeys.QC_OBJECTPOOL_MAX_SIZE_DEFAULT),
      QueryCacheServer.conf.getInt(QCConfigKeys.QC_OBJECTPOOL_CELL_COEFF,
        QCConfigKeys.QC_OBJECTPOOL_CELL_COEFF_DEFAULT),
      QueryCacheServer.conf.getLong(QCConfigKeys.QC_OBJECTPOOL_RESIZING_CYCLE_MILLI,
        QCConfigKeys.QC_OBJECTPOOL_RESIZING_CYCLE_MILLI_DEFAULT),
      QueryCacheServer.conf.getFloat(QCConfigKeys.QC_OBJECTPOOL_RESIZING_F,
        QCConfigKeys.QC_CONNECTIONPOOL_FREE_RESIZING_F_DEFAULT)
    );

  private static CLIHandler handlerInstance = null;
  public static CLIHandler getInstance() {
    if (handlerInstance == null) {
      handlerInstance = new CLIHandler();
    }
    return handlerInstance;
  }

  protected CLIHandler() {
    if (gConnMgr.initialize() == CORE_RESULT.CORE_FAILURE) {
      LOG.error("Server start failed.");
      System.exit(1);
    }
  }
  
  /**
   * OpenSession()
   *
   *
   * Open a session (connection) on the server against which operations may be executed
   *
   */

  public TOpenSessionResp OpenSession(TOpenSessionReq aReq) {
    LOG.debug("OpenSession is requested.");
    long startTime = 0;
    long endTime;
    String user;
    String password;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    
    TOpenSessionResp sResp = new TOpenSessionResp();
    sResp.setStatus(new TStatus());
    ConnNode sConn = null;
    
    if (aReq.clientProtocol != new TOpenSessionReq().clientProtocol) {
      LOG.warn("Protocol version mismatched. -client: " + aReq.clientProtocol);
    }
    
    // 1. trim the connection string
    String sUrl = aReq.url.trim();
    
    // TODO: we need to add authentication process
    // 2. check id/pass from auhentication db
    user = aReq.username;
    password = aReq.password;
    
    // 3. alloc a ConnNode
    sConn = gConnMgr.allocConn(sUrl);
    int sNumRetry = 3;
    for (int i = 0 ; (sConn == null) && (i < sNumRetry) ; i++) {
      // retry to get connection to the backend
      sConn = gConnMgr.allocConn(sUrl);
    }
    
    if (sConn == null) {
      LOG.error(
        "Connection error : there is no connection in the pool or unknown connection type error. (url: "
        + sUrl + " )");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage(
        "Connection error : there is no connection in the pool or unknown connection type error. (url: "
        + sUrl + " )");
      THostInfo sHI = new THostInfo();
      buildHostInfo(sHI);
      sResp.setHostInfo(sHI);
      sResp.setSessionHandle(new TSessionHandle(
        new THandleIdentifier().setDriverType(sUrl)));
      return sResp;
    }
    
    // TODO: 4. set connection properties by TOpenSessionReq.configuration
    sConn.user = user;
    sConn.setPassword(password);
    sConn.clientInfo = aReq.getHostInfo();
    
    // 5. build TOpenSessionResp for responding to the client
    //sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    sResp.status.setStatusCode(TStatusCode.SUCCESS_STATUS);
    THostInfo sHI = new THostInfo();
    buildHostInfo(sHI);
    sResp.setHostInfo(sHI);
    TSessionHandle sSH = new TSessionHandle(
      new THandleIdentifier(sConn.sConnId, 0, sUrl));
    sResp.setSessionHandle(sSH);
    
    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("OpenSession PROFILE: UserID=" + user + ", ConnID=" + sConn.sConnId +
        ", Url=" + sUrl + ", Connection time elapsed=" + (endTime-startTime) + "ms");
      sConn.latency[0] = endTime-startTime;
    }
    
    return sResp;
  }
  
  private void buildHostInfo(THostInfo aHostInfo) {
    try {
      aHostInfo.hostname = InetAddress.getLocalHost().getHostName();
      aHostInfo.ipaddr = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      LOG.info("HostInfo can't be built properly.");
    }
    aHostInfo.portnum = QueryCacheServer.conf.getInt(QCConfigKeys.QC_SERVER_PORT, 8655);
  }

  /**
   * CloseSession()
   *
   * Closes the specified session and frees any resources currently allocated
   * to that session. Any open operations in that session will be closed.
   * 
   */
  public TCloseSessionResp CloseSession(TCloseSessionReq aReq) {
    LOG.debug("CloseSession is requested.");
    long startTime = 0;
    long endTime;
    ConnNode sConn = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }

    TCloseSessionResp sResp = new TCloseSessionResp();
    sResp.setStatus(new TStatus().setStatusCode(TStatusCode.SUCCESS_STATUS));

    sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType,
            aReq.sessionHandle.sessionId.connid);
    if (sConn != null) {
      // 1. find the specific ConnNode by ConnId
      if (gConnMgr.closeConn(aReq.sessionHandle.sessionId.driverType,
              aReq.sessionHandle.sessionId.connid) == CORE_RESULT.CORE_FAILURE) {
        // just ignoring the failure, client don't need to handle this failure.
        LOG.error("CloseSession failed.");
      }


      if (profileLvl > 0) {
        endTime = System.currentTimeMillis();
        sConn.latency[1] = endTime - startTime;
        LOG.info("CloseSession PROFILE: ConnID=" + sConn.sConnId + ", Type=" +
                aReq.sessionHandle.sessionId.driverType +
                ", Close time elapsed=" + (endTime - startTime) + "ms" +
                ", OpenClose_latency={" + sConn.latency[0] + "," + sConn.latency[1] + "}");
        sConn.latency[0] = sConn.latency[1] = -1;
      }
    }
    
    return sResp;
  }

  /**
   * GetInfo()
   *
   * This function is based on ODBC's CLIGetInfo() function.
   *
   */
  public TGetInfoResp GetInfo(TGetInfoReq aReq) {
    return new TGetInfoResp();
  }

  /**
   * ExecuteStatement()
   *
   * Execute a statement.
   *
   */
  /*
  struct TExecuteStatementReq {
    1: required TSessionHandle sessionHandle

    // The statment to be executed
    2: required string statement

    3: optional map<string, string> configuration
  }

  struct TExecuteStatementResp {
    1: required TStatus status
    2: optional TOperationHandle operationHandle
  }
  */

  public TExecuteStatementResp ExecuteStatement(TExecuteStatementReq aReq) {
    LOG.debug("ExecuteStatement is requested.");
    long startTime = 0;
    long endTime;
    long timeArr[] = {-1,-1,-1,-1,-1,-1};
    int i = 0;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      timeArr[i++] = startTime = System.currentTimeMillis();
    }
    gConnMgr.runtimeProfile.increaseNumReq();
    //
    // 1. get connection from pool
    //
    long sConnId = aReq.sessionHandle.sessionId.connid;
    long sStmtId = 0;
    
    TExecuteStatementResp sResp = new TExecuteStatementResp();
    sResp.setStatus(new TStatus());

    ConnNode sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("ExecuteStatement error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("ExecuteStatement error : the connection doesn't exist.");
      return sResp;
    }
    if (profileLvl > 1)
      timeArr[i++] = System.currentTimeMillis();

    //
    // 2. alloc statement
    //
    try {
      sStmt = sConn.allocStmt(true);
      // set profiling data
      sStmt.profile.clientIp = sConn.clientInfo.getHostname() + "/" + sConn.clientInfo.getIpaddr();
      sStmt.profile.stmtState = StmtNode.State.EXEC;
      sStmt.profile.connType = aReq.sessionHandle.sessionId.driverType;
      sStmt.profile.execProfile = timeArr;
      sStmt.profile.queryStr = aReq.statement.replace('\n',' ').replace('\r',' ').replace('\t',' ');
      sStmtId = sStmt.sStmtId;
      sQueryId = sConnId + ":" + sStmtId;
      // TODO: 2.5 set statement properties by TExecuteStatementReq.configuration
      //    (e.g. setMaxRows())

      if (profileLvl > 1)
        timeArr[i++] = System.currentTimeMillis();
      
      //
      // 3. Analyze SQL stmt
      //
      boolean isAuthCheck = 
        !QueryCacheServer.conf.get(QCConfigKeys.QC_AUTHORIZATION_PREFIX + "." +
          aReq.sessionHandle.sessionId.driverType.split(":")[1], QCConfigKeys.QC_AUTHORIZATION_DEFAULT).
            equalsIgnoreCase("NONE");
      if (isSyntaxCheck || isAuthCheck) {
        try {
          Analyzer compiler = new Analyzer(sConn.user,
            aReq.sessionHandle.sessionId.driverType, gConnMgr);
          compiler.analyzer(aReq.statement, sStmt.profile, isAuthCheck);
        } catch (AuthorizationException e) {
          LOG.error("Authorization error:" + e.getMessage() +
            "\n  -Error Query: " + aReq.statement, e);
          sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
          sResp.status.setErrorMessage(e.getMessage());
          gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
            sQueryId, State.ERROR);
          return sResp;
        } catch (AnalyzerException e) {
          LOG.error("Query analyzer error:" + e.getMessage() +
            "\n  -Error Query: " + aReq.statement, e);
          sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
          sResp.status.setErrorMessage(e.getMessage());
          gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
            sQueryId, State.ERROR);
          return sResp;
        }
      }
      if (profileLvl > 1)
        timeArr[i++] = System.currentTimeMillis();
      
      //
      // 4. execute query
      //
      try {
      sStmt.sHStmt.setFetchSize(_defFetchSize);
      } catch (SQLFeatureNotSupportedException e) {
        //ignore
      }
      boolean sIsRS = sStmt.sHStmt.execute(aReq.statement);
      if (profileLvl > 1)
        timeArr[i++] = System.currentTimeMillis();
      
      //
      // 5. build TExecuteStatementResp
      //
      THandleIdentifier sHI = new THandleIdentifier(sConnId, sStmtId,
        aReq.sessionHandle.sessionId.driverType);
      TOperationHandle sOH = new TOperationHandle();
      sOH.setOperationId(sHI);
      sOH.setOperationType(TOperationType.EXECUTE_STATEMENT);
      sResp.setOperationHandle(sOH);
      
      if (sIsRS) {
        sResp.operationHandle.hasResultSet = true;
        // 6. Save a ResultSet in the StmtNode
        sStmt.sHasResultSet = true;
        sStmt.sRS = sStmt.sHStmt.getResultSet();
      } else {
        sResp.operationHandle.hasResultSet = false;
        sResp.operationHandle.updateRowCount = sStmt.sHStmt.getUpdateCount();
        // set profiling data
        sStmt.profile.rowCnt = (long)sResp.operationHandle.updateRowCount;
      }
      
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("ExecuteStatement error(" + e.getSQLState() + ") :" + e.getMessage() +
        "\n  -Error Query: " + aReq.statement, e);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setSqlState(e.getSQLState());
      sResp.status.setErrorCode(e.getErrorCode());
      sResp.status.setErrorMessage(e.getMessage());
      // remove failed ConnNode in the UsingMap
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.sessionHandle.sessionId.driverType, sConnId);
        LOG.warn("ExecuteStatement: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("ExecuteStatement PROFILE: QueryID=" + sConn.sConnId + ":" + sStmtId
        + ", Type=" +aReq.sessionHandle.sessionId.driverType + ", Execute time elapsed="
        + (endTime-startTime) + "ms" + ", Query=" + sStmt.profile.queryStr);
      sStmt.profile.timeHistogram[0] = endTime - startTime;
      if (profileLvl > 1) {
        timeArr[i++] = endTime;
      }
    }
    
    return sResp;
  }

  /**
   * GetTypeInfo()
   *
   * Get information about types supported by the QueryCache.
   *
   */
  public TGetTypeInfoResp GetTypeInfo(TGetTypeInfoReq aReq) {
    LOG.debug("GetTypeInfo is requested.");
    long startTime = 0;
    long endTime;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    gConnMgr.runtimeProfile.increaseNumReq();
    //
    // 1. get connection from pool
    //
    long sConnId = aReq.sessionHandle.sessionId.connid;
    long sStmtId = 0;
    
    TGetTypeInfoResp sResp = new TGetTypeInfoResp();
    sResp.setStatus(new TStatus());

    ConnNode sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("GetTypeInfo error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("GetTypeInfo error : the connection doesn't exist.");
      return sResp;
    }

    //
    // 2. alloc a fake statement
    //
    try {
      sStmt = sConn.allocStmt(false);
      // set profiling data
      sStmt.profile.clientIp = sConn.clientInfo.getHostname() + "/" + sConn.clientInfo.getIpaddr();
      sStmt.profile.stmtState = StmtNode.State.GETTYPEINFO;
      sStmt.profile.connType = aReq.sessionHandle.sessionId.driverType;
      sStmt.profile.execProfile = null;
      sStmt.profile.queryStr = "";
      sStmtId = sStmt.sStmtId;
      sQueryId = sConnId + ":" + sStmtId;
      
      // TODO: 3. authorization
      
      //
      // 4. GetTypeInfo
      //
      DatabaseMetaData dmd = sConn.sHConn.getMetaData();
      sStmt.sRS = dmd.getTypeInfo();
      sStmt.sHasResultSet = true;

      //
      // 5. build TGetCatalogsResp
      //
      THandleIdentifier sHI = new THandleIdentifier(sConnId, sStmtId,
        aReq.sessionHandle.sessionId.driverType);
      TOperationHandle sOH = new TOperationHandle();
      sOH.setOperationId(sHI);
      sOH.setHasResultSet(true);
      sOH.setOperationType(TOperationType.GET_TYPE_INFO);
      sResp.setOperationHandle(sOH);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("GetTypeInfo error(" + e.getSQLState() + ") :" + e.getMessage() + "\n", e);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setSqlState(e.getSQLState());
      sResp.status.setErrorCode(e.getErrorCode());
      sResp.status.setErrorMessage(e.getMessage());
      // remove failed ConnNode in the UsingMap
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.sessionHandle.sessionId.driverType, sConnId);
        LOG.warn("GetTypeInfo: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
              sQueryId, State.ERROR);
      
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("GetTypeInfo PROFILE: QueryID=" + sConn.sConnId + ":" + sStmtId
        + ", Type=" +aReq.sessionHandle.sessionId.driverType + ", GetTypeInfo time elapsed="
        + (endTime-startTime) + "ms");
      sStmt.profile.timeHistogram[1] = endTime - startTime;
    }
    
    return sResp;
  }

  /**
   * GetCatalogs()
   *
   * Returns the list of databases
   * Result are ordered by TABLE_CATALOG
   *
   */
  public TGetCatalogsResp GetCatalogs(TGetCatalogsReq aReq) {
    LOG.debug("GetCatalogs is requested.");
    long startTime = 0;
    long endTime;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    gConnMgr.runtimeProfile.increaseNumReq();
    //
    // 1. get connection from pool
    //
    long sConnId = aReq.sessionHandle.sessionId.connid;
    long sStmtId = 0;
    
    TGetCatalogsResp sResp = new TGetCatalogsResp();
    sResp.setStatus(new TStatus());

    ConnNode sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("GetCatalogs error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("GetCatalogs error : the connection doesn't exist.");
      return sResp;
    }

    //
    // 2. alloc a fake statement
    //
    try {
      sStmt = sConn.allocStmt(false);
      // set profiling data
      sStmt.profile.clientIp = sConn.clientInfo.getHostname() + "/" + sConn.clientInfo.getIpaddr();
      sStmt.profile.stmtState = StmtNode.State.GETCATALOGS;
      sStmt.profile.connType = aReq.sessionHandle.sessionId.driverType;
      sStmt.profile.execProfile = null;
      sStmt.profile.queryStr = "";
      sStmtId = sStmt.sStmtId;
      sQueryId = sConnId + ":" + sStmtId;
      
      // TODO: 3. authorization
      
      //
      // 4. getCatalogs
      //
      DatabaseMetaData dmd = sConn.sHConn.getMetaData();
      sStmt.sRS = dmd.getCatalogs();
      sStmt.sHasResultSet = true;

      //
      // 5. build TGetCatalogsResp
      //
      THandleIdentifier sHI = new THandleIdentifier(sConnId, sStmtId,
        aReq.sessionHandle.sessionId.driverType);
      TOperationHandle sOH = new TOperationHandle();
      sOH.setOperationId(sHI);
      sOH.setHasResultSet(true);
      sOH.setOperationType(TOperationType.GET_CATALOGS);
      sResp.setOperationHandle(sOH);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("GetCatalogs error(" + e.getSQLState() + ") :" + e.getMessage() + "\n", e);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setSqlState(e.getSQLState());
      sResp.status.setErrorCode(e.getErrorCode());
      sResp.status.setErrorMessage(e.getMessage());
      // remove failed ConnNode in the UsingMap
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.sessionHandle.sessionId.driverType, sConnId);
        LOG.warn("GetCatalogs: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("GetCatalogs PROFILE: QueryID=" + sConn.sConnId + ":" + sStmtId
        + ", Type=" +aReq.sessionHandle.sessionId.driverType + ", GetCatalogs time elapsed="
        + (endTime-startTime) + "ms");
      sStmt.profile.timeHistogram[1] = endTime - startTime;
    }
    
    return sResp;
  }

  /**
   * GetSchemas()
   *
   * Returns the schema names available in this database
   * The results are ordered by TABLE_CATALOG and TABLE_SCHEM
   *
   */
  public TGetSchemasResp GetSchemas(TGetSchemasReq aReq) {
    LOG.debug("GetSchemas is requested.");
    long startTime = 0;
    long endTime;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    gConnMgr.runtimeProfile.increaseNumReq();
    //
    // 1. get connection from pool
    //
    long sConnId = aReq.sessionHandle.sessionId.connid;
    long sStmtId = 0;
    
    TGetSchemasResp sResp = new TGetSchemasResp();
    sResp.setStatus(new TStatus());

    ConnNode sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("GetSchemas error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("GetSchemas error : the connection doesn't exist.");
      return sResp;
    }

    //
    // 2. alloc a fake statement
    //
    try {
      sStmt = sConn.allocStmt(false);
      // set profiling data
      sStmt.profile.clientIp = sConn.clientInfo.getHostname() + "/" + sConn.clientInfo.getIpaddr();
      sStmt.profile.stmtState = StmtNode.State.GETSCHEMAS;
      sStmt.profile.connType = aReq.sessionHandle.sessionId.driverType;
      sStmt.profile.execProfile = null;
      sStmt.profile.queryStr = "";
      sStmtId = sStmt.sStmtId;
      sQueryId = sConnId + ":" + sStmtId;
      
      // TODO: 3. authorization
      
      //
      // 4. getSchemas
      //
      DatabaseMetaData dmd = sConn.sHConn.getMetaData();
      sStmt.sRS = dmd.getSchemas(
          (aReq.isSetCatalogName()?aReq.getCatalogName():null),
          (aReq.isSetSchemaName()?aReq.getSchemaName():null));
      sStmt.sHasResultSet = true;

      //
      // 5. build TGetSchemasResp
      //
      THandleIdentifier sHI = new THandleIdentifier(sConnId, sStmtId,
        aReq.sessionHandle.sessionId.driverType);
      TOperationHandle sOH = new TOperationHandle();
      sOH.setOperationId(sHI);
      sOH.setHasResultSet(true);
      sOH.setOperationType(TOperationType.GET_SCHEMAS);
      sResp.setOperationHandle(sOH);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("GetSchemas error(" + e.getSQLState() + ") :" + e.getMessage() + "\n", e);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setSqlState(e.getSQLState());
      sResp.status.setErrorCode(e.getErrorCode());
      sResp.status.setErrorMessage(e.getMessage());
      // remove failed ConnNode in the UsingMap
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.sessionHandle.sessionId.driverType, sConnId);
        LOG.warn("GetSchemas: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("GetSchemas PROFILE: QueryID=" + sConn.sConnId + ":" + sStmtId
        + ", Type=" +aReq.sessionHandle.sessionId.driverType + ", GetSchemas time elapsed="
        + (endTime-startTime) + "ms");
      sStmt.profile.timeHistogram[1] = endTime - startTime;
    }
    
    return sResp;
  }

  /**
   * GetTables()
   *
   * Returns a list of tables with catalog, schema, and table type information.
   * Results are ordered by TABLE_TYPE, TABLE_CAT, TABLE_SCHEM, and TABLE_NAME
   *
   */
  public TGetTablesResp GetTables(TGetTablesReq aReq) {
    LOG.debug("GetTables is requested.");
    long startTime = 0;
    long endTime;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    gConnMgr.runtimeProfile.increaseNumReq();
    //
    // 1. get connection from pool
    //
    long sConnId = aReq.sessionHandle.sessionId.connid;
    long sStmtId = 0;
    
    TGetTablesResp sResp = new TGetTablesResp();
    sResp.setStatus(new TStatus());

    ConnNode sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("GetTables error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("GetTables error : the connection doesn't exist.");
      return sResp;
    }

    //
    // 2. alloc a fake statement
    //
    try {
      sStmt = sConn.allocStmt(false);
      // set profiling data
      sStmt.profile.clientIp = sConn.clientInfo.getHostname() + "/" + sConn.clientInfo.getIpaddr();
      sStmt.profile.stmtState = StmtNode.State.GETTABLES;
      sStmt.profile.connType = aReq.sessionHandle.sessionId.driverType;
      sStmt.profile.execProfile = null;
      sStmt.profile.queryStr = "";
      sStmtId = sStmt.sStmtId;
      sQueryId = sConnId + ":" + sStmtId;
      
      // TODO: 3. authorization
      
      //
      // 4. getTables
      //
      DatabaseMetaData dmd = sConn.sHConn.getMetaData();
      sStmt.sRS = dmd.getTables(
        (aReq.isSetCatalogName()?aReq.getCatalogName():null),
        (aReq.isSetSchemaName()?aReq.getSchemaName():null),
        (aReq.isSetTableName()?aReq.getTableName():null),
        (aReq.isSetTableTypes()?aReq.getTableTypes().toArray(new String[aReq.getTableTypes().size()]):null));
      sStmt.sHasResultSet = true;

      //
      // 5. build TGetSchemasResp
      //
      THandleIdentifier sHI = new THandleIdentifier(sConnId, sStmtId,
        aReq.sessionHandle.sessionId.driverType);
      TOperationHandle sOH = new TOperationHandle();
      sOH.setOperationId(sHI);
      sOH.setHasResultSet(true);
      sOH.setOperationType(TOperationType.GET_TABLES);
      sResp.setOperationHandle(sOH);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("GetTables error(" + e.getSQLState() + ") :" + e.getMessage() + "\n", e);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setSqlState(e.getSQLState());
      sResp.status.setErrorCode(e.getErrorCode());
      sResp.status.setErrorMessage(e.getMessage());
      // remove failed ConnNode in the UsingMap
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.sessionHandle.sessionId.driverType, sConnId);
        LOG.warn("GetTables: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("GetTables PROFILE: QueryID=" + sConn.sConnId + ":" + sStmtId
        + ", Type=" +aReq.sessionHandle.sessionId.driverType + ", GetTables time elapsed="
        + (endTime-startTime) + "ms");
      sStmt.profile.timeHistogram[1] = endTime - startTime;
    }
    
    return sResp;
  }

  /**
   * GetTableTypes()
   *
   * Returns the table types available in this database.
   * The results are ordered by table type.
   * 
   */
  public TGetTableTypesResp GetTableTypes(TGetTableTypesReq aReq) {
    LOG.debug("GetTableTypes is requested.");
    long startTime = 0;
    long endTime;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    gConnMgr.runtimeProfile.increaseNumReq();
    //
    // 1. get connection from pool
    //
    long sConnId = aReq.sessionHandle.sessionId.connid;
    long sStmtId = 0;
    
    TGetTableTypesResp sResp = new TGetTableTypesResp();
    sResp.setStatus(new TStatus());

    ConnNode sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("GetTableTypes error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("GetTableTypes error : the connection doesn't exist.");
      return sResp;
    }

    //
    // 2. alloc a fake statement
    //
    try {
      sStmt = sConn.allocStmt(false);
      // set profiling data
      sStmt.profile.clientIp = sConn.clientInfo.getHostname() + "/" + sConn.clientInfo.getIpaddr();
      sStmt.profile.stmtState = StmtNode.State.GETTABLETYPES;
      sStmt.profile.connType = aReq.sessionHandle.sessionId.driverType;
      sStmt.profile.execProfile = null;
      sStmt.profile.queryStr = "";
      sStmtId = sStmt.sStmtId;
      sQueryId = sConnId + ":" + sStmtId;
      
      // TODO: 3. authorization
      
      //
      // 4. getTableTypes
      //
      DatabaseMetaData dmd = sConn.sHConn.getMetaData();
      sStmt.sRS = dmd.getTableTypes();
      sStmt.sHasResultSet = true;

      //
      // 5. build TGetSchemasResp
      //
      THandleIdentifier sHI = new THandleIdentifier(sConnId, sStmtId,
        aReq.sessionHandle.sessionId.driverType);
      TOperationHandle sOH = new TOperationHandle();
      sOH.setOperationId(sHI);
      sOH.setHasResultSet(true);
      sOH.setOperationType(TOperationType.GET_TABLE_TYPES);
      sResp.setOperationHandle(sOH);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("GetTableTypes error(" + e.getSQLState() + ") :" + e.getMessage() + "\n", e);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setSqlState(e.getSQLState());
      sResp.status.setErrorCode(e.getErrorCode());
      sResp.status.setErrorMessage(e.getMessage());
      // remove failed ConnNode in the UsingMap
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.sessionHandle.sessionId.driverType, sConnId);
        LOG.warn("GetTableTypes: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("GetTableTypes PROFILE: QueryID=" + sConn.sConnId + ":" + sStmtId
        + ", Type=" +aReq.sessionHandle.sessionId.driverType + ", GetTableTypes time elapsed="
        + (endTime-startTime) + "ms");
      sStmt.profile.timeHistogram[1] = endTime - startTime;
    }
    
    return sResp;
  }

  /**
   * GetColumns()
   *
   * Returns a list of columns in the specified tables.
   * Results are ordered by TABLE_CAT, TABLE_SCHEM, TABLE_NAME, and
   * ORDINAL_POSITION
   * 
   */
  public TGetColumnsResp GetColumns(TGetColumnsReq aReq) {
    LOG.debug("GetColumns is requested.");
    long startTime = 0;
    long endTime;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    gConnMgr.runtimeProfile.increaseNumReq();
    //
    // 1. get connection from pool
    //
    long sConnId = aReq.sessionHandle.sessionId.connid;
    long sStmtId = 0;
    
    TGetColumnsResp sResp = new TGetColumnsResp();
    sResp.setStatus(new TStatus());

    ConnNode sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("GetColumns error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("GetColumns error : the connection doesn't exist.");
      return sResp;
    }

    //
    // 2. alloc a fake statement
    //
    try {
      sStmt = sConn.allocStmt(false);
      // set profiling data
      sStmt.profile.clientIp = sConn.clientInfo.getHostname() + "/" + sConn.clientInfo.getIpaddr();
      sStmt.profile.stmtState = StmtNode.State.GETCOLUMNS;
      sStmt.profile.connType = aReq.sessionHandle.sessionId.driverType;
      sStmt.profile.execProfile = null;
      sStmt.profile.queryStr = "";
      sStmtId = sStmt.sStmtId;
      sQueryId = sConnId + ":" + sStmtId;
      
      // TODO: 3. authorization
      
      //
      // 4. GetColumns
      //
      DatabaseMetaData dmd = sConn.sHConn.getMetaData();
      sStmt.sRS = dmd.getColumns(
        (aReq.isSetCatalogName()?aReq.getCatalogName():null),
        (aReq.isSetSchemaName()?aReq.getSchemaName():null),
        (aReq.isSetTableName()?aReq.getTableName():null),
        (aReq.isSetColumnName()?aReq.getColumnName():null));
      sStmt.sHasResultSet = true;

      //
      // 5. build TGetSchemasResp
      //
      THandleIdentifier sHI = new THandleIdentifier(sConnId, sStmtId,
        aReq.sessionHandle.sessionId.driverType);
      TOperationHandle sOH = new TOperationHandle();
      sOH.setOperationId(sHI);
      sOH.setHasResultSet(true);
      sOH.setOperationType(TOperationType.GET_COLUMNS);
      sResp.setOperationHandle(sOH);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("GetColumns error(" + e.getSQLState() + ") :" + e.getMessage() + "\n", e);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setSqlState(e.getSQLState());
      sResp.status.setErrorCode(e.getErrorCode());
      sResp.status.setErrorMessage(e.getMessage());
      // remove failed ConnNode in the UsingMap
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.sessionHandle.sessionId.driverType, sConnId);
        LOG.warn("GetColumns: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("GetColumns PROFILE: QueryID=" + sConn.sConnId + ":" + sStmtId
        + ", Type=" +aReq.sessionHandle.sessionId.driverType + ", GetColumns time elapsed="
        + (endTime-startTime) + "ms");
      sStmt.profile.timeHistogram[1] = endTime - startTime;
    }
    
    return sResp;
  }

  /**
   * GetOperationStatus()
   *
   * Get the status of an operation running on the server.
   * We don't need this method yet, because all APIs we supported are working synchronously.
   * 
   */
  public TGetOperationStatusResp GetOperationStatus(TGetOperationStatusReq aReq) {
    return new TGetOperationStatusResp();
  }

  public void internalCancelStatement(String sQueryId, String driverType) {
    LOG.info("InternalCancelOperation is requested.");
    long sConnId;
    long sStmtId;
    long startTime = 0;
    long endTime;

    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }

    try {
      String[] token = sQueryId.split(":");
      sConnId = Long.valueOf(token[0]);
      sStmtId = Long.valueOf(token[1]);
    }
    catch (Exception e) {
      LOG.error("mal-formatted query ID (" + sQueryId + ", " + driverType + ")");
      return;
    }

    ConnNode sConn = gConnMgr.getConn(driverType, sConnId);
    if (sConn == null) {
      LOG.error("invalid query ID (" + sQueryId + ", " + driverType + ")");
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
              sQueryId, State.ERROR);
      return;
    }

    // 2. Cancel the statement
    StmtNode sStmt = null;
    try {
      sStmt = sConn.cancelStmt(sStmtId);
    } catch (SQLException e) {
      LOG.error("Error cancelling query (" + e.getSQLState() + ") :" + e.getMessage(), e);
    } finally {
      gConnMgr.removeConn(driverType, sConnId);
      LOG.info("Removing a cancelled connection (connId:" + sConn.sConnId + ")");
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("InternalCancelOperation PROFILE: QueryId=" + sConnId + ":" + sStmtId + ", Type="
              + driverType + ", CancelOp time elapsed="
              + (endTime-startTime) + "ms");

      if(sStmt != null) {
        // move runtimeProfile to completeProfile Map
        CLIHandler.gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
                sQueryId, StmtNode.State.CANCEL);
      }
    }
  }

  /**
   * CancelOperation()
   *
   * Cancels processing on the specified operation handle and
   * frees any resources which were allocated.
   *
   */
  public TCancelOperationResp CancelOperation(TCancelOperationReq aReq) {
    LOG.debug("CancelOperation is requested.");
    long startTime = 0;
    long endTime;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    TCancelOperationResp sResp = new TCancelOperationResp();
    sResp.setStatus(new TStatus());
    
    // 1. find statement and remove from statement UsingMap
    long sConnId = aReq.operationHandle.operationId.connid;
    long sStmtId = aReq.operationHandle.operationId.stmtid;
    sQueryId = sConnId + ":" + sStmtId;
    
    ConnNode sConn = gConnMgr.getConn(aReq.operationHandle.operationId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("CancelOperation error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("CancelOperation error : the connection doesn't exist.");
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    
    // 2. Cancel the statement
    try {
      sStmt = sConn.cancelStmt(sStmtId);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("CancelOperation error (" + e.getSQLState() + ") :" + e.getMessage(), e);
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.sqlState = e.getSQLState();
      sResp.status.errorCode = e.getErrorCode();
      sResp.status.errorMessage = e.getMessage();
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.operationHandle.operationId.driverType, sConnId);
        LOG.warn("CancelOperation: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("CancelOperation PROFILE: QueryId=" + sConnId + ":" + sStmtId + ", Type="
        + aReq.operationHandle.operationId.driverType + ", CancelOp time elapsed="
        + (endTime-startTime) + "ms");
      
      if(sStmt != null) {
        // move runtimeProfile to completeruntimeProfile Map
        CLIHandler.gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
          sQueryId, StmtNode.State.CANCEL);
      }
    }
    return sResp;
  }

  /**
   * CloseOperation()
   *
   * This will free all of the resources which allocated on
   * the server to service the operation.
   * 
   */
  public TCloseOperationResp CloseOperation(TCloseOperationReq aReq) {
    LOG.debug("CloseOperation is requested.");
    long startTime = 0;
    long endTime;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    TCloseOperationResp sResp = new TCloseOperationResp();
    sResp.setStatus(new TStatus());
    
    // 1. find statement and remove from statement UsingMap
    long sConnId = aReq.operationHandle.operationId.connid;
    long sStmtId = aReq.operationHandle.operationId.stmtid;
    sQueryId = sConnId + ":" + sStmtId;
    
    ConnNode sConn = gConnMgr.getConn(aReq.operationHandle.operationId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("CloseOperation error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("CloseOperation error : the connection doesn't exist.");
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    
    // 2. close the statement
    try {
      sStmt = sConn.closeStmt(sStmtId);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      if ("HY000".equals(e.getSQLState())) {
        LOG.info("CloseOperation: closing a canceled statement");
      } else {
        LOG.error("CloseOperation error (" + e.getSQLState() + ") :" + e.getMessage(), e);
      }
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.sqlState = e.getSQLState();
      sResp.status.errorCode = e.getErrorCode();
      sResp.status.errorMessage = e.getMessage();
      // remove failed ConnNode in the ConnPool
      gConnMgr.removeConn(aReq.operationHandle.operationId.driverType, sConnId);
      LOG.info("CloseOperation: Removing a failed connection (connId:" + sConn.sConnId + ")");
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("CloseOperation PROFILE: QueryId=" + sConnId + ":" + sStmtId + ", Type="
        + aReq.operationHandle.operationId.driverType + ", CloseOp time elapsed="
        + (endTime-startTime) + "ms");
      
      if(sStmt != null) {
        sStmt.profile.endTime = endTime;
        sStmt.profile.timeHistogram[3] = endTime - startTime;
        // move runtimeProfile to completeruntimeProfile Map
        CLIHandler.gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
          sQueryId, StmtNode.State.CLOSE);
      
        long totalT = (sStmt.profile.timeHistogram[0]
            + sStmt.profile.timeHistogram[1] + sStmt.profile.timeHistogram[2] + sStmt.profile.timeHistogram[3]);
        LOG.info("#QUERY PROFILE:QueryId=" + sConnId + ":" + sStmtId + ",User="
            + sConn.user + "Query=\"" + sStmt.profile.queryStr + "\""
            + ",Type=" + aReq.operationHandle.operationId.driverType
            + ",RowCnt=" + sStmt.profile.rowCnt + ",ElapedTime=" + totalT
            + ",State=SUCCESS");

        if (totalT >= QueryCacheServer.conf.getLong(
            QCConfigKeys.QC_QUERY_PROFILING_DETAIL_UPPER_MILLI,
            QCConfigKeys.QC_QUERY_PROFILING_DETAIL_UPPER_MILLI_DEFAULT)) {
          String profile = "\n    -Execquery : "
              + sStmt.profile.timeHistogram[0];
          if (profileLvl > 1 && sStmt.profile.execProfile != null) {
            profile += "\n      - #1. GetConn     : "
                + (sStmt.profile.execProfile[1] - sStmt.profile.execProfile[0])
                + "\n      - #2. AllocStmt   : "
                + (sStmt.profile.execProfile[2] - sStmt.profile.execProfile[1])
                + "\n      - #3. AnalyzeStmt : "
                + (sStmt.profile.execProfile[3] - sStmt.profile.execProfile[2])
                + "\n      - #4. ExecStmt    : "
                + (sStmt.profile.execProfile[4] - sStmt.profile.execProfile[3])
                + "\n      - #5. Build resp  : "
                + (sStmt.profile.execProfile[5] - sStmt.profile.execProfile[4]);
          }
          profile += "\n    -Getmeta   : " + sStmt.profile.timeHistogram[1]
              + "\n    -Fetch     : " + sStmt.profile.timeHistogram[2];
          //1-0=sumGetConn,2-1=sumGetStmt,3=endGetStmt,4-3=initFetcher,5-4=getMeta,6-5=1stFetch,
          //7=sumNext,8=sumGetCol,9=lastNext,10=EndTS
          if (profileLvl > 1 && sStmt.profile.fetchProfile != null) {
            profile += "\n      - #1. GetConn(sum) : "
                + (sStmt.profile.fetchProfile[1] - sStmt.profile.fetchProfile[0])
                + "\n      - #2. GetStmt(sum) : "
                + (sStmt.profile.fetchProfile[2] - sStmt.profile.fetchProfile[1])
                + "\n      - #3. InitFetcher  : "
                + (sStmt.profile.fetchProfile[4] - sStmt.profile.fetchProfile[3])
                + "\n      - #4. GetMeta      : "
                + (sStmt.profile.fetchProfile[5] - sStmt.profile.fetchProfile[4]);
            switch (profileLvl) {
              case 2:
                profile += "\n      - #5. Fetch        : "
                    + (sStmt.profile.fetchProfile[6] - sStmt.profile.fetchProfile[5]);
                break;
              case 3:
                profile += "\n      - #5. Fetch"
                    + "\n            - First fetch   : "
                    + (sStmt.profile.fetchProfile[6] - sStmt.profile.fetchProfile[5])
                    + "\n            - FetchRows(sum): "
                    + (sStmt.profile.fetchProfile[7])
                    + "\n            - GetCols(sum)  : "
                    + (sStmt.profile.fetchProfile[8])
                    + "\n      - #6. Build resp   : "
                    + (sStmt.profile.fetchProfile[10] - sStmt.profile.fetchProfile[9]);
                break;
              default:
                break;
            }
          }
          profile += "\n    -Stmtclose : " + sStmt.profile.timeHistogram[3];
          LOG.info("#Detail profile: QueryID=" + sQueryId + profile);
        }
      }
    }
    return sResp;
  }

  /**
   * GetResultSetMetadata()
   *
   * Retrieves schema information for the specified operation
   *
   */
  /*
struct TGetResultSetMetadataReq {
  1: required TOperationHandle operationHandle
}

struct TTableSchema {
  1: required list<MetaData.TColumnDesc> columns
}

struct TColumnDesc {
  1: required string columnName

  2: required Types.TColumnType columnType

  // Ordinal position in the source table
  3: required i32 position

  4: optional string comment

  // Stats for this table, if any are available
  // 5: optional TColumnStats col_stats
}

struct TColumnType {
  1: required TTypeId type

  // Only set if type == CHAR
  2: optional i32 len

  // Only set if type == DECIMAL
  3: optional i32 position
  4: optional i32 scale
}

struct TGetResultSetMetadataResp {
  1: required TStatus status
  2: optional TTableSchema schema
}
   */
  public TGetResultSetMetadataResp GetResultSetMetadata(TGetResultSetMetadataReq aReq) {
    LOG.debug("GetResultSetMetadata is requested.");
    long startTime = 0;
    long endTime;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    TGetResultSetMetadataResp sResp = new TGetResultSetMetadataResp();
    sResp.setStatus(new TStatus());
    
    //
    // 1. get ResultSetMetaData from ResultSet
    //
    long sConnId = aReq.operationHandle.operationId.connid;
    long sStmtId = aReq.operationHandle.operationId.stmtid;
    sQueryId = sConnId + ":" + sStmtId;
    
    ConnNode sConn = gConnMgr.getConn(aReq.operationHandle.operationId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("GetResultSetMetadata error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "GetResultSetMetadata error : The connection doesn't exist.";
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    
    StmtNode sStmt = sConn.sStmtMap.get(sStmtId);
    if (sStmt == null) {
      // the statement doesn't even exist, just send a error msg.
      LOG.error("GetResultSetMetadata error : the statement doesn't exist." +
        " (id:" + sStmtId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "GetResultSetMetadata error : The statement doesn't exist.";
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    if (sStmt.isCanceled) {
      sResp.status.errorMessage = "Canceled";
      sResp.status.sqlState = "HY000";
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      return sResp;
    }
    
    // set profiling data
    sStmt.profile.stmtState = StmtNode.State.GETMETA;
    
    if (sStmt.sHasResultSet != true) {
      LOG.error("GetResultSetMetadata error : The statement has no ResultSet." +
        " (id:" + sStmtId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "GetResultSetMetadata error : The statement has no ResultSet.";
      return sResp;
    }
    
    try {
      ResultSet sRS = sStmt.sRS;
      ResultSetMetaData sMeta = sRS.getMetaData();
      
      //
      // 2. build TTableSchma for each of columns
      //
      TTableSchema sTblSchema = new TTableSchema();
      
      int sColCnt = sMeta.getColumnCount();
      for (int i = 1; i <= sColCnt; i++) {
        TColumnDesc sColDesc = new TColumnDesc(sMeta.getColumnName(i),
                                               new TTypeDesc(), i);
        TTypeEntry sColType = TTypeEntry.primitiveEntry(new TPrimitiveTypeEntry());
        TTypeId sQCType = mapSQL2QCType(sMeta.getColumnType(i));
        switch (sQCType) {
          case CHAR:
            sColType.getPrimitiveEntry().setLen(sMeta.getPrecision(i));
            break;
          case DECIMAL:
            sColType.getPrimitiveEntry().setScale(sMeta.getScale(i));
            break;
          default:
            break;
        }
        sColType.getPrimitiveEntry().setType(sQCType);
        // The "top" type is always the first element of the list.
        // If the top type is an ARRAY, MAP, STRUCT, or UNIONTYPE
        // type, then subsequent elements represent nested types.
        sColDesc.typeDesc.addToTypes(sColType);
        
        sTblSchema.addToColumns(sColDesc);
      }
      sResp.setSchema(sTblSchema);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("GetResultSetMetadata error (" + e.getSQLState() + ") :" + e.getMessage(), e);
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.sqlState = e.getSQLState();
      sResp.status.errorCode = e.getErrorCode();
      sResp.status.errorMessage = e.getMessage();
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.operationHandle.operationId.driverType, sConnId);
        LOG.warn("GetResultSetMetadata: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("GetResultSetMetadata PROFILE: QueryId=" + sConnId + ":" + sStmtId + ", Type="
        + aReq.operationHandle.operationId.driverType
        + ", GetResultSetMetadata time elapsed=" + (endTime-startTime) + "ms");
      sStmt.profile.timeHistogram[1] = endTime - startTime;
    }
    return sResp;
  }

  /**
   * FetchResults()
   *
   * Fetch rows from server corresponding to a particular OperationHandle
   *
   */
  /*
  struct TFetchResultsReq {
    1: required TOperationHandle operationHandle
    2: required TFetchOrientation orientation = TFetchOrientation.FETCH_NEXT
    // unlimited
    3: required i64 maxRows = -1
  }
  struct TFetchResultsResp {
    1: required TStatus status
    2: optional bool hasMoreRows
    3: optional Data.TRowSet results
    4: optional i64 numofrows
  }
  struct TRowSet {
    // 0-based
    1: required i64 startRowOffset
    2: required list<TRow> rows
    3: optional TTableSchema schema
  }
  struct TRow {
    1: list<TColumnValue> colVals
  }
  union TColumnValue {
    1: bool boolVal      // BOOLEAN
    2: byte byteVal      // TINYINT, CHAR
    3: i16 shortVal      // SMALLINT
    4: i32 intVal        // INT
    5: i64 longVal       // BIGINT, TIMESTAMP
    6: double doubleVal  // FLOAT, DOUBLE
    // string or any binary column data can be stored
    7: string stringVal  // STRING, LIST, MAP, STRUCT, BINARY, DECIMAL
  }
  */

  public TFetchResultsResp FetchResults(TFetchResultsReq aReq) {
    LOG.debug("FetchResults is requested.");
    long startTime = 0;
    long tmpTime = 0;
    long endTime = 0;
    int  j = 0;
    long fetchSize = aReq.getMaxRows();
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    
    TFetchResultsResp sResp = new TFetchResultsResp();
    sResp.setStatus(new TStatus());
    
    //
    // 1. get Conn
    //
    long sConnId = aReq.operationHandle.operationId.connid;
    long sStmtId = aReq.operationHandle.operationId.stmtid;
    sQueryId = sConnId + ":" + sStmtId;
    
    ConnNode sConn = gConnMgr.getConn(aReq.operationHandle.operationId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("FetchResults error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "FetchResults error : the connection doesn't exist.";
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    if (profileLvl > 1)
      tmpTime = System.currentTimeMillis();
    
    //
    // 2. get Stmt
    //
    StmtNode sStmt = sConn.sStmtMap.get(sStmtId);
    if (sStmt == null) {
      // the statement doesn't even exist, just send a error msg.
      LOG.error("FetchResults error : the statement doesn't exist." +
        " (id:" + sStmtId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "FetchResults error : the statement doesn't exist.";
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    if (sStmt.isCanceled) {
      sResp.status.errorMessage = "Canceled";
      sResp.status.sqlState = "HY000";
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      return sResp;
    }
    
    //1-0=sumGetConn,2-1=sumGetStmt,3=endGetStmt,4-3=initFetcher,5-4=getMeta,6-5=1stFetch,
    //7=sumNext,8=sumGetCol,9=lastNext,10=EndTS
    long timeArr[] = sStmt.profile.fetchProfile;
    sStmt.profile.stmtState = StmtNode.State.FETCH;

    if (sStmt.sHasResultSet != true) {
      LOG.error("FetchResults error : The statement has no ResultSet." +
        " (id:" + sStmtId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "FetchResults error : The statement has no ResultSet.";
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    if (profileLvl > 1) {
      timeArr[j++] += startTime; //index-0
      timeArr[j++] += tmpTime; //index-1
      tmpTime = System.currentTimeMillis();
      timeArr[j++] += tmpTime; //index-2
      if (timeArr[j++] == -1)
        timeArr[j-1] = tmpTime; // index-3
    }
    
    RowFetcher rowFetcher = sStmt.allocRowProducer(sQueryId, this);
    try {
      //if (rowFetcher.getAndSetIfInit(FetchState.FETCHING) == FetchState.INIT) {
    if (rowFetcher._isFetching.compareAndSet(false, true)) {
        _threadPool.execute(rowFetcher);
      }
    } catch (RejectedExecutionException e) {
      LOG.error("FetchResults error : Producer thread unable to be executed.");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "FetchResults error : Producer thread unable to be executed.";
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      return sResp;
    }
    List<TRow> rowsFromProducer = rowFetcher.getRows(fetchSize);
    TRowSet sRowSet = (TRowSet)gObjPool.getObject(ObjectPool.POOL_TROWSET);
    rowFetcher.addRowSet(sRowSet); // for object recycling

    // 0-based
    sRowSet.clear();
    if (rowsFromProducer == null) {
      // to cope with no rows returned case
      sRowSet.rows = new ArrayList<TRow>();
    } else {
      sRowSet.rows = rowsFromProducer;
    }
    
    sResp.setHasMoreRows(rowFetcher._hasMoreRows.get());
    sResp.setResults(sRowSet);
    sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("Consumer fetchResults PROFILE: QueryId=" + sQueryId + ", Type="
        + aReq.operationHandle.operationId.driverType + ", RowCnt=" + sRowSet.rows.size()
        + ", Fetch time elapsed=" + (endTime-startTime) + "ms");
    }
    return sResp;
  }
  
  public TGetFunctionsResp GetFunctions(TGetFunctionsReq aReq) {
    LOG.debug("GetFunctions is requested.");
    long startTime = 0;
    long endTime;
    StmtNode sStmt = null;
    String sQueryId = null;
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    gConnMgr.runtimeProfile.increaseNumReq();
    //
    // 1. get connection from pool
    //
    long sConnId = aReq.sessionHandle.sessionId.connid;
    long sStmtId = 0;
    
    TGetFunctionsResp sResp = new TGetFunctionsResp();
    sResp.setStatus(new TStatus());

    ConnNode sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("GetFunctions error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("GetFunctions error : the connection doesn't exist.");
      return sResp;
    }

    //
    // 2. alloc a fake statement
    //
    try {
      sStmt = sConn.allocStmt(false);
      // set profiling data
      sStmt.profile.clientIp = sConn.clientInfo.getHostname() + "/" + sConn.clientInfo.getIpaddr();
      sStmt.profile.stmtState = StmtNode.State.GETFUNCTIONS;
      sStmt.profile.connType = aReq.sessionHandle.sessionId.driverType;
      sStmt.profile.execProfile = null;
      sStmt.profile.queryStr = "";
      sStmtId = sStmt.sStmtId;
      sQueryId = sConnId + ":" + sStmtId;
      
      // TODO: 3. authorization
      
      //
      // 4. getFunctions
      //
      DatabaseMetaData dmd = sConn.sHConn.getMetaData();
      sStmt.sRS = dmd.getFunctions(
        (aReq.isSetCatalogName()?aReq.getCatalogName():null),
        (aReq.isSetSchemaName()?aReq.getSchemaName():null),
        (aReq.isSetFunctionName()?aReq.getFunctionName():null));
      sStmt.sHasResultSet = true;

      //
      // 5. build TGetSchemasResp
      //
      THandleIdentifier sHI = new THandleIdentifier(sConnId, sStmtId,
        aReq.sessionHandle.sessionId.driverType);
      TOperationHandle sOH = new TOperationHandle();
      sOH.setOperationId(sHI);
      sOH.setHasResultSet(true);
      sOH.setOperationType(TOperationType.GET_FUNCTIONS);
      sResp.setOperationHandle(sOH);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("GetFunctions error(" + e.getSQLState() + ") :" + e.getMessage() + "\n", e);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setSqlState(e.getSQLState());
      sResp.status.setErrorCode(e.getErrorCode());
      sResp.status.setErrorMessage(e.getMessage());
      // remove failed ConnNode in the UsingMap
      if ("08S01".equals(e.getSQLState())) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.sessionHandle.sessionId.driverType, sConnId);
        LOG.warn("GetFunctions: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      gConnMgr.runtimeProfile.moveRunToCompleteProfileMap(
        sQueryId, State.ERROR);
      
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("GetFunctions PROFILE: QueryID=" + sConn.sConnId + ":" + sStmtId
        + ", Type=" +aReq.sessionHandle.sessionId.driverType + ", GetFunctions time elapsed="
        + (endTime-startTime) + "ms");
      sStmt.profile.timeHistogram[1] = endTime - startTime;
    }
    
    return sResp;
  }

  public TGetLogResp GetLog(TGetLogReq req) {
    return new TGetLogResp();
  }

  public TTypeId mapSQL2QCType(int aJDBCType) {
    TTypeId sInterT;
    switch (aJDBCType) {
      case java.sql.Types.BIGINT:
        sInterT = TTypeId.BIGINT;
        break;
      case java.sql.Types.BINARY:
        sInterT = TTypeId.BINARY;
        break;
      case java.sql.Types.BOOLEAN:
        sInterT = TTypeId.BOOLEAN;
        break;
      case java.sql.Types.CHAR:
        sInterT = TTypeId.CHAR;
        break;
      case java.sql.Types.DATE:  // DATATIME?
        sInterT = TTypeId.DATE;
        break;
      case java.sql.Types.DECIMAL:
        sInterT = TTypeId.DECIMAL;
        break;
      case java.sql.Types.DOUBLE:
        sInterT = TTypeId.DOUBLE;
        break;
      case java.sql.Types.FLOAT:
        sInterT = TTypeId.FLOAT;
        break;
      case java.sql.Types.INTEGER:
        sInterT = TTypeId.INT;
        break;
      case java.sql.Types.SMALLINT:
        sInterT = TTypeId.SMALLINT;
        break;
      case java.sql.Types.TIMESTAMP:
        sInterT = TTypeId.TIMESTAMP;
        break;
      case java.sql.Types.TINYINT:
        sInterT = TTypeId.TINYINT;
        break;
      case java.sql.Types.VARCHAR:
        sInterT = TTypeId.STRING;
        break;
      case java.sql.Types.ARRAY:
        //sInterT = TTypeId.ARRAY;
        //break;
      case java.sql.Types.STRUCT:
        //sInterT = TTypeId.STRUCT;
        //break;
      case java.sql.Types.BIT:
      case java.sql.Types.BLOB:
      case java.sql.Types.CLOB:
      case java.sql.Types.DATALINK:
      case java.sql.Types.DISTINCT:
      case java.sql.Types.JAVA_OBJECT:
      case java.sql.Types.LONGNVARCHAR:
      case java.sql.Types.LONGVARBINARY:
      case java.sql.Types.LONGVARCHAR:
      case java.sql.Types.NCHAR:
      case java.sql.Types.NCLOB:
      case java.sql.Types.NULL:
      case java.sql.Types.NUMERIC:
      case java.sql.Types.NVARCHAR:
      case java.sql.Types.OTHER:
      case java.sql.Types.REAL:
      case java.sql.Types.REF:
      case java.sql.Types.ROWID:
      case java.sql.Types.SQLXML:
      case java.sql.Types.TIME:
      case java.sql.Types.VARBINARY:
        LOG.warn("Unsupported SQL type is detected. (" + aJDBCType + ")");
        sInterT = TTypeId.UNSUPPORTED;
        break;
      default:
        LOG.warn("Unknown SQL type is detected.");
        sInterT = TTypeId.UNKNOWN;
        break;
    }
    return sInterT;
  }
  
  public static ObjectPool getObjPool() {
    return gObjPool;
  }

  public static int getThreadPoolSize()
  {
    return ((ThreadPoolExecutor)_threadPool).getPoolSize();
  }
  public static int getThreadPoolActiveCount()
  {
    return ((ThreadPoolExecutor)_threadPool).getActiveCount();
  }

  /*
  private ConnType convertSesstypeToConntype (TSessionType aSessType) {
    switch (aSessType) {
      case SESS_PHOENIX_JDBC:
        return ConnType.PHOENIX_JDBC;
      case SESS_IMPALA_JDBC:
        return ConnType.IMPALA_JDBC;
      case SESS_HIVE_JDBC:
        return ConnType.HIVE_JDBC;
      case SESS_MYSQL_JDBC:
        return ConnType.MYSQL_JDBC;
      default:
        return ConnType.UNKNOWN;
    }
  }*/
}
