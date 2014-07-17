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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.thrift.TException;

// Generated code
import com.skplanet.querycache.thrift.*;
import com.skplanet.querycache.cli.thrift.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.common.*;
import com.skplanet.querycache.server.common.InternalType.CORE_RESULT;
import com.skplanet.querycache.server.sqlcompiler.Analyzer;
import com.skplanet.querycache.server.util.ObjectPool;
import com.skplanet.querycache.server.util.ObjectPool.TargetObjs;
import com.skplanet.querycache.server.sqlcompiler.AuthorizationException;

public class CLIHandler implements TCLIService.Iface {
  private static final Logger LOG = LoggerFactory.getLogger(CLIHandler.class);
  
  private final int profileLvl = QueryCacheServer.conf.getInt(QCConfigKeys.QC_QUERY_PROFILING_LEVEL,
    QCConfigKeys.QC_QUERY_PROFILING_LEVEL_DEFAULT);
  private final boolean isSyntaxCheck =
    QueryCacheServer.conf.getBoolean(QCConfigKeys.QC_QUERY_SYNTAX_CHECK,
      QCConfigKeys.QC_QUERY_SYNTAX_CHECK_DEFAULT);

  private ConnMgr gConnMgr = new ConnMgr();
  
  private ObjectPool gObjPool =
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

  public CLIHandler() {
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
      aHostInfo.ipaddr = InetAddress.getLocalHost().toString();
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
      sConn = gConnMgr.getConn(aReq.sessionHandle.sessionId.driverType,
          aReq.sessionHandle.sessionId.connid);
    }
    
    // 1. find the specific ConnNode by ConnId
    if (gConnMgr.closeConn(aReq.sessionHandle.sessionId.driverType,
          aReq.sessionHandle.sessionId.connid) == CORE_RESULT.CORE_FAILURE) {
      // just ignoring the failure, client don't need to handle this failure.
      LOG.error("CloseSession failed.");
    }
    
    TCloseSessionResp sResp = new TCloseSessionResp();
    sResp.setStatus(new TStatus().setStatusCode(TStatusCode.SUCCESS_STATUS));
    
    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      sConn.latency[5] = endTime-startTime;
      long total = 0;
      for (int i = 0; i < 6; i++) {
        total += sConn.latency[i];
      }
      LOG.info("CloseSession PROFILE: ConnID=" + sConn.sConnId + ", Type=" + 
          aReq.sessionHandle.sessionId.driverType + 
          ", Close time elapsed=" + (endTime-startTime) + "ms" +
          ", #Total lanency=" + total + "ms");
      if (total >= 
            QueryCacheServer.conf.getLong(
              QCConfigKeys.QC_QUERY_PROFILING_DETAIL_UPPER_MILLI,
              QCConfigKeys.QC_QUERY_PROFILING_DETAIL_UPPER_MILLI_DEFAULT)) {
        String profile =   
            "\n    -Open      : " + sConn.latency[0]
          + "\n    -Execquery : " + sConn.latency[1];
        if (profileLvl > 1 && sConn.execPofile != null) {
          profile +=
            "\n      - #1. GetConn     : " + (sConn.execPofile[1] - sConn.execPofile[0])
          + "\n      - #2. AllocStmt   : " + (sConn.execPofile[2] - sConn.execPofile[1])
          + "\n      - #3. AnalyzeStmt : " + (sConn.execPofile[3] - sConn.execPofile[2])
          + "\n      - #4. ExecStmt    : " + (sConn.execPofile[4] - sConn.execPofile[3])
          + "\n      - #5. Build resp  : " + (sConn.execPofile[5] - sConn.execPofile[4]);
        }
        profile += 
            "\n    -Getmeta   : " + sConn.latency[2]
          + "\n    -Fetch     : " + sConn.latency[3];
        if (profileLvl > 1 && sConn.fetchProfile != null) {
          profile += 
              "\n      - #1. GetConn      : " + (sConn.fetchProfile[1] - sConn.fetchProfile[0])
            + "\n      - #2. GetStmt      : " + (sConn.fetchProfile[2] - sConn.fetchProfile[1])
            + "\n      - #3. GetMeta      : " + (sConn.fetchProfile[3] - sConn.fetchProfile[2]);
          switch (profileLvl) {
            case 2:
              profile += 
              "\n      - #4. Fetch        : " + (sConn.fetchProfile[4] - sConn.fetchProfile[3]);
              break;
            case 3:
              profile += 
              "\n      - #4. Fetch"
            + "\n            - First fetch   : " + (sConn.fetchProfile[4] - sConn.fetchProfile[3])
            + "\n            - FetchRows(sum): " + (sConn.fetchProfile[5])
            + "\n            - GetCols(sum)  : " + (sConn.fetchProfile[6])
            + "\n      - #5. Build resp   : " + (sConn.fetchProfile[8] - sConn.fetchProfile[7]);
              break;
            default:
              break;
          }
        }
        profile += 
            "\n    -Stmtclose : " + sConn.latency[4]
          + "\n    -Connclose : " + sConn.latency[5];
        LOG.info("###Detail profile: ConnID=" + sConn.sConnId + profile);
      }
      for (int i = 0; i < sConn.latency.length; i++) {
        sConn.latency[i] = 0;
      }
      sConn.execPofile = sConn.fetchProfile = null;
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
    long timeArr[] = new long[10];
    int i = 0;
    StmtNode sStmt = null;
    
    if (profileLvl > 0) {
      timeArr[i++] = startTime = System.currentTimeMillis();
    }
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
      sStmt   = sConn.allocStmt();
      sStmtId = sStmt.sStmtId;
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
          compiler.analyzer(aReq.statement, isAuthCheck);
        } catch (AuthorizationException e) {
          if (isAuthCheck) {
            LOG.error("Authorization error:" + e.getMessage() +
                "\n  -Error Query: " + aReq.statement, e);
            sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
            sResp.status.setErrorMessage(e.getMessage());
            return sResp;
          }
        } catch (AnalyzerException e) {
          if (isSyntaxCheck) {
            LOG.error("Query syntax error:" + e.getMessage() +
                "\n  -Error Query: " + aReq.statement, e);
            sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
            sResp.status.setErrorMessage(e.getMessage());
            return sResp;
          }
        }
      }
      if (profileLvl > 1)
        timeArr[i++] = System.currentTimeMillis();
      
      //
      // 4. execute query
      //
      boolean sIsRS = sStmt.sHStmt.execute(aReq.statement);
      sStmt.sState = StmtNode.State.RUNNING;
      sStmt.sQuery = aReq.statement;
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
      if (e.getSQLState().equals("08S01")) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.sessionHandle.sessionId.driverType, sConnId);
        LOG.warn("ExecuteStatement: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("ExecuteStatement PROFILE: ConnID=" + sConn.sConnId + ", StmtId=" + sStmtId
        + ", Type=" +aReq.sessionHandle.sessionId.driverType + ", Execute time elapsed="
        + (endTime-startTime) + "ms" + ", Query=" + aReq.statement);
      sConn.latency[1] = endTime-startTime;
      if (profileLvl > 1) {
        timeArr[i++] = endTime;
        sConn.execPofile = timeArr;
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
    return new TGetTypeInfoResp();
  }

  /**
   * GetCatalogs()
   *
   * Returns the list of databases
   * Result are ordered by TABLE_CATALOG
   *
   */
  public TGetCatalogsResp GetCatalogs(TGetCatalogsReq aReq) {
    return new TGetCatalogsResp();
  }

  /**
   * GetSchemas()
   *
   * Returns the schema names available in this database
   * The results are ordered by TABLE_CATALOG and TABLE_SCHEM
   *
   */
  public TGetSchemasResp GetSchemas(TGetSchemasReq aReq) {
    return new TGetSchemasResp();
  }

  /**
   * GetTables()
   *
   * Returns a list of tables with catalog, schema, and table type information.
   * Results are ordered by TABLE_TYPE, TABLE_CAT, TABLE_SCHEM, and TABLE_NAME
   *
   */
  public TGetTablesResp GetTables(TGetTablesReq aReq) {
    return new TGetTablesResp();
  }

  /**
   * GetTableTypes()
   *
   * Returns the table types available in this database.
   * The results are ordered by table type.
   * 
   */
  public TGetTableTypesResp GetTableTypes(TGetTableTypesReq aReq) {
    return new TGetTableTypesResp();
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
    return new TGetColumnsResp();
  }

  /**
   * GetOperationStatus()
   *
   * Get the status of an operation running on the server.
   * 
   */
  public TGetOperationStatusResp GetOperationStatus(TGetOperationStatusReq aReq) {
    return new TGetOperationStatusResp();
  }

  /**
   * CancelOperation()
   *
   * Cancels processing on the specified operation handle and
   * frees any resources which were allocated.
   *
   */
  public TCancelOperationResp CancelOperation(TCancelOperationReq aReq) {
    return new TCancelOperationResp();
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
    
    if (profileLvl > 0) {
      startTime = System.currentTimeMillis();
    }
    TCloseOperationResp sResp = new TCloseOperationResp();
    sResp.setStatus(new TStatus());
    
    // 1. find statement and remove from statement UsingMap
    long sConnId = aReq.operationHandle.operationId.connid;
    long sStmtId = aReq.operationHandle.operationId.stmtid;
    
    ConnNode sConn = gConnMgr.getConn(aReq.operationHandle.operationId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("CloseOperation error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("CloseOperation error : the connection doesn't exist.");
      return sResp;
    }
    
    // 2. close the statement
    try {
      sStmt = sConn.closeStmt(sStmtId);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("CloseOperation error (" + e.getSQLState() + ") :" + e.getMessage(), e);
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.sqlState = e.getSQLState();
      sResp.status.errorCode = e.getErrorCode();
      sResp.status.errorMessage = e.getMessage();
      if (e.getSQLState().equals("08S01")) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.operationHandle.operationId.driverType, sConnId);
        LOG.warn("CloseOperation: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      return sResp;
    }
    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("CloseOperation PROFILE: ConnId=" + sConnId + ", StmtId=" + sStmtId + ", Type="
        + aReq.operationHandle.operationId.driverType + ", CloseOp time elapsed="
        + (endTime-startTime) + "ms");
      sConn.latency[4] = endTime-startTime;
      LOG.info("#QUERY PROFILE(" + sConnId + ":" + sStmtId + "): Query= \"" + sStmt.sQuery
          + "\"" + ", Type=" + aReq.operationHandle.operationId.driverType 
          + ", RowCnt=" + sStmt.rowCnt + ", ExecFetch time elapsed="
          + (sConn.latency[1]+sConn.latency[2]+sConn.latency[3]+sConn.latency[4]) + "ms");
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
    
    ConnNode sConn = gConnMgr.getConn(aReq.operationHandle.operationId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("GetResultSetMetadata error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "GetResultSetMetadata error : The connection doesn't exist.";
      return sResp;
    }
    
    StmtNode sStmt = sConn.sStmtMap.get(sStmtId);
    if (sStmt == null) {
      // the statement doesn't even exist, just send a error msg.
      LOG.error("GetResultSetMetadata error : the statement doesn't exist." +
        " (id:" + sStmtId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "GetResultSetMetadata error : The statement doesn't exist.";
      return sResp;
    }
    
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
      sStmt.sState = StmtNode.State.RUNNING;
      
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
      if (e.getSQLState().equals("08S01")) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.operationHandle.operationId.driverType, sConnId);
        LOG.warn("GetResultSetMetadata: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      return sResp;
    }

    if (profileLvl > 0) {
      endTime = System.currentTimeMillis();
      LOG.info("GetResultSetMetadata PROFILE: ConnId=" + sConnId + ", StmtId=" + sStmtId + ", Type="
        + aReq.operationHandle.operationId.driverType
        + ", GetResultSetMetadata time elapsed=" + (endTime-startTime) + "ms");
      sConn.latency[2] = endTime-startTime;
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
  }
  struct TRowSet {
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
    long endTime, midTime;
    long timeArr[] = new long[10];
    int  j = 0, k = 0;
    long sRowCount = 0;
    
    if (profileLvl > 0) {
      timeArr[j++] = startTime = System.currentTimeMillis();
    }
    
    TFetchResultsResp sResp = new TFetchResultsResp();
    sResp.setStatus(new TStatus());
    
    //
    // 1. get Conn
    //
    long sConnId = aReq.operationHandle.operationId.connid;
    long sStmtId = aReq.operationHandle.operationId.stmtid;
    
    ConnNode sConn = gConnMgr.getConn(aReq.operationHandle.operationId.driverType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("FetchResults error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "FetchResults error : the connection doesn't exist.";
      return sResp;
    }
    if (profileLvl > 1)
      timeArr[j++] = System.currentTimeMillis();
    
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
      return sResp;
    }
    
    if (sStmt.sHasResultSet != true) {
      LOG.error("FetchResults error : The statement has no ResultSet." +
        " (id:" + sStmtId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "FetchResults error : The statement has no ResultSet.";
      return sResp;
    }
    if (profileLvl > 1)
      timeArr[j++] = System.currentTimeMillis();
    
    //
    // 3. Fetch each row and save to TRowSet
    //
    try {
      ResultSet sRS = sStmt.sRS;
      ResultSetMetaData sMeta = sRS.getMetaData();
      /*
      if ( sRS.isAfterLast() == true ) {
        LOG.error("FetchResults error : The statement has no ResultSet." +
            " (id:" + sStmtId + ")");
          sResp.status.statusCode = TStatusCode.ERROR_STATUS;
          sResp.status.errorMessage = "FetchResults error : The statement has no ResultSet.";
          return sResp;
      }*/
      
      TRowSet sRowSet = (TRowSet)gObjPool.getObject(TargetObjs.TROWSET);
      if (sRowSet == null) {
        sRowSet = new TRowSet();
      }
      // 0-based
      sRowSet.clear();
      // to cope with no rows returned case
      sRowSet.rows = new ArrayList<TRow>();
      sStmt.sState = StmtNode.State.FETCHING;
      
      int sColCnt = sMeta.getColumnCount();
      if (profileLvl > 1)
        timeArr[j++] = System.currentTimeMillis(); // T0
      
      // TODO: Below code block should be more optimized for performance
      k = j;
      while (sRS.next()) {
        ++sRowCount;
        TRow sRow = (TRow)gObjPool.getObject(TargetObjs.TROW);
        if (sRow == null) {
          sRow = new TRow();
        }
        if (profileLvl > 2) {
          midTime = System.currentTimeMillis(); // T1
          if (sRowCount == 1) {
            // put first-fetch TS
            timeArr[j++] = midTime;
            k = j;
          }
          j = k;
          // put the sum of T1 to calculate avg time of next()
          timeArr[j++] += midTime;
        }
        for (int i = 1; i <= sColCnt; i++) {
          TColumnValue sCell = (TColumnValue)gObjPool.getObject(TargetObjs.TCOLUMNVALUE);
          if (sCell == null) {
            sCell = new TColumnValue();
          }
          TTypeId sQCType = mapSQL2QCType(sMeta.getColumnType(i));
          switch (sQCType) {
            case CHAR:
            case STRING:
            case BINARY:
            case DECIMAL:
              String value = sRS.getString(i);
              TStringValue sStrV = (TStringValue)gObjPool.getObject(TargetObjs.TSTRINGVALUE);
              if (sStrV != null) {
                sCell.setStringVal(sStrV.setValue(value));
              } else {
                sCell.setStringVal(new TStringValue().setValue(value));
              }
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
        sRowSet.addToRows(sRow);
        if (profileLvl > 2) {
          midTime = System.currentTimeMillis(); // T2
          // put the sum of T2 to calculate avg time of get cols
          timeArr[j++] += midTime;
          // we also need TS of the last T2
          timeArr[j++] = midTime;
        }
      }
      
      if (profileLvl > 2) {
        if (sRowCount > 0) {
          // calculate sum time of next and get cols
          // sum of next() = (sum from i=2 to n of TS1 - sum from i=1 to n-1 of TS2)
          // sum of get cols = (sum from i=1 to n of TS3 - sum from i=1 to n of TS2)
          midTime = timeArr[k];
          timeArr[k] =
            ((timeArr[k]-timeArr[k-1])-(timeArr[k+1]-timeArr[k+2]));
          timeArr[k+1] = (timeArr[k+1] - midTime);
        }
      }

      sResp.hasMoreRows = false;
      sResp.numofrows = sStmt.rowCnt = sRowCount;
      sResp.results = sRowSet;
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("FetchResults error (" + e.getSQLState() + ") :" + e.getMessage(), e);
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.sqlState = e.getSQLState();
      sResp.status.errorCode = e.getErrorCode();
      sResp.status.errorMessage = e.getMessage();
      if (e.getSQLState().equals("08S01")) {
        // remove failed ConnNode in the ConnPool
        gConnMgr.removeConn(aReq.operationHandle.operationId.driverType, sConnId);
        LOG.warn("FetchResults: Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
      return sResp;
    }
    if (profileLvl > 0) {
      endTime = System.currentTimeMillis(); // T3
      LOG.info("FetchResults PROFILE: ConnId=" + sConnId + ", StmtId=" + sStmtId + ", Type="
        + aReq.operationHandle.operationId.driverType + ", Fetch time elapsed="
        + (endTime-startTime) + "ms");
      sConn.latency[3] = endTime-startTime;
      if (profileLvl > 1) {
        if (profileLvl > 2 && sRowCount == 0) {
          timeArr[j++] = endTime;
          timeArr[j++] = timeArr[j++] = 0;
          timeArr[j++] = endTime;
        }
        timeArr[j++] = endTime;
        sConn.fetchProfile = timeArr;
      }
    }
    return sResp;
  }
  
  private TTypeId mapSQL2QCType(int aJDBCType) {
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
