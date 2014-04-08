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
import com.skplanet.querycache.server.util.ObjectPool;
import com.skplanet.querycache.server.util.ObjectPool.TargetObjs;

public class CLIHandler implements TCLIService.Iface {
  private static final Logger LOG = LoggerFactory.getLogger(CLIHandler.class);
  
  static private ConnMgr gConnMgr = new ConnMgr();
  
  static private ObjectPool gObjPool = new ObjectPool(Configure.gMaxPoolSize,
                                                      Configure.gReloadCycle,
                                                      Configure.gObjPoolResizingF);

  public CLIHandler() {
    if (gConnMgr.initialize(Configure.gConnPoolFreeInitSize,
                        Configure.gConnPoolFreeResizingF)
        == CORE_RESULT.CORE_FAILURE) {
      LOG.error("Server start failed.");
      System.exit(1);
    }
  }
  
  /*
   * OpenSession()
   *
   *
   * Open a session (connection) on the server against which operations may be executed
   *
   */

  public TOpenSessionResp OpenSession(TOpenSessionReq aReq) {
    LOG.info("OpenSession is requested.");
    long startTime;
    long endTime;
    
    if (Configure.gQueryProfile) {
      startTime = System.currentTimeMillis();
    }
    
    TOpenSessionResp sResp = new TOpenSessionResp();
    sResp.setStatus(new TStatus());
    ConnNode sConn = null;
    
    if (aReq.clientProtocol != new TOpenSessionReq().clientProtocol) {
      LOG.warn("Protocol version mismatched. -client: " + aReq.clientProtocol);
    }
    
    // 1. check the connection string
    String sUrl = aReq.url;
    ConnType sSType;
    TSessionType sSessType;
    int sInd = sUrl.indexOf(':');
    if (sInd == -1) {
      LOG.error("Connection string syntax error :" + sUrl);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("Connection string syntax error :" + sUrl);
      return sResp;
    }
    String sProtocol = sUrl.substring(0, sInd);
    String sService = sUrl.substring(sInd + 1);
    if (sProtocol.equals("jdbc")) {
      if (sService.equals("bdb")) {
        sSType = ConnType.PHOENIX_JDBC;
        sSessType = TSessionType.SESS_PHOENIX_JDBC;
      } else if (sService.equals("impala")) {
        sSType = ConnType.IMPALA_JDBC;
        sSessType = TSessionType.SESS_IMPALA_JDBC;
      } else if (sService.equals("hive")) {
        sSType = ConnType.HIVE_JDBC;
        sSessType = TSessionType.SESS_HIVE_JDBC;
      } else if (sService.equals("mysql")) {
        sSType = ConnType.MYSQL_JDBC;
        sSessType = TSessionType.SESS_MYSQL_JDBC;
      } else {
        // unsupported yet
        LOG.error("Unsupported storage type error :" + sUrl);
        sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
        sResp.status.setErrorMessage("Unsupported storage type error :" + sUrl);
        return sResp;
      }
    } else {
      // unsupported yet
      LOG.error("Unsupported storage type error :" + sUrl);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("Unsupported storage type error :" + sUrl);
      return sResp;
    }
    
    // TODO: we need to add authentication process
    // 2. check id/pass from auhentication db
    
    // 3. alloc a ConnNode
    sConn = gConnMgr.allocConn(sSType);
    int sNumRetry = 3;
    for (int i = 0 ; (sConn == null) && (i < sNumRetry) ; i++) {
      // retry to get connection to the backend
      sConn = gConnMgr.allocConn(sSType);
    }
    
    if (sConn == null) {
      LOG.error("Connection error : there is no connection in the pool.");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("Connection error : there is no connection in the pool.");
      THostInfo sHI = new THostInfo();
      buildHostInfo(sHI);
      sResp.setHostInfo(sHI);
      return sResp;
    }
    
    // TODO: 4. set connection properties by TOpenSessionReq.configuration
    
    // 5. build TOpenSessionResp for responding to the client
    //sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    sResp.status.setStatusCode(TStatusCode.SUCCESS_STATUS);
    THostInfo sHI = new THostInfo();
    buildHostInfo(sHI);
    sResp.setHostInfo(sHI);
    TSessionHandle sSH = new TSessionHandle(
        new THandleIdentifier(sConn.sConnId, 0, sSessType));
    sResp.setSessionHandle(sSH);
    
    if (Configure.gQueryProfile) {
      endTime = System.currentTimeMillis();
      LOG.info("PROFILE: Connection time elapsed : " + (endTime-startTime) + "ms");
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
    aHostInfo.portnum = Configure.gServerPort;
  }

  /*
   * CloseSession()
   *
   * Closes the specified session and frees any resources currently allocated
   * to that session. Any open operations in that session will be closed.
   * 
   */
  public TCloseSessionResp CloseSession(TCloseSessionReq aReq) {
    LOG.info("CloseSession is requested.");
    long startTime;
    long endTime;
    
    if (Configure.gQueryProfile) {
      startTime = System.currentTimeMillis();
    }
    
    ConnType sConnType = convertSesstypeToConntype(aReq.sessionHandle.sessionId.driverType);
    // 1. find the specific ConnNode by ConnId
    if (gConnMgr.closeConn(sConnType, aReq.sessionHandle.sessionId.connid) == 
        CORE_RESULT.CORE_FAILURE) {
      // just ignoring the failure, client don't need to handle this failure.
      LOG.error("CloseSession failed.");
    }
    
    TCloseSessionResp sResp = new TCloseSessionResp();
    sResp.setStatus(new TStatus().setStatusCode(TStatusCode.SUCCESS_STATUS));
    
    if (Configure.gQueryProfile) {
      endTime = System.currentTimeMillis();
      LOG.info("PROFILE: Close time elapsed : " + (endTime-startTime) + "ms");
    }
    
    return sResp;
  }

  /*
   * GetInfo()
   *
   * This function is based on ODBC's CLIGetInfo() function.
   *
   */
  public TGetInfoResp GetInfo(TGetInfoReq aReq) {
    return new TGetInfoResp();
  }

  /*
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
    LOG.info("ExecuteStatement is requested.");
    long startTime;
    long endTime;
    
    if (Configure.gQueryProfile) {
      startTime = System.currentTimeMillis();
    }
    // 1. get connection from pool
    long sConnId = aReq.sessionHandle.sessionId.connid;
    long sStmtId = 0;
    ConnType sConnType = convertSesstypeToConntype(aReq.sessionHandle.sessionId.driverType);
    
    TExecuteStatementResp sResp = new TExecuteStatementResp();
    sResp.setStatus(new TStatus());

    ConnNode sConn = gConnMgr.getConn(sConnType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("ExecuteStatement error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setErrorMessage("ExecuteStatement error : the connection doesn't exist.");
      return sResp;
    }

    // 2. alloc statement
    try {
      StmtNode sStmt = sConn.allocStmt();
      sStmtId = sStmt.sStmtId;
      // TODO: 3. set statement properties by TExecuteStatementReq.configuration
      //    (e.g. setMaxRows())
      
      // 4. execute query
      boolean sIsRS = sStmt.sHStmt.execute(aReq.statement);
      sStmt.sState = StmtNode.State.RUNNING;
      
      // 5. build TExecuteStatementResp
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
      LOG.error("ExecuteStatement error :" + e.getMessage(), e);
      sResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
      sResp.status.setSqlState(e.getSQLState());
      sResp.status.setErrorCode(e.getErrorCode());
      sResp.status.setErrorMessage(e.getMessage());
      return sResp;
    }

    if (Configure.gQueryProfile) {
      endTime = System.currentTimeMillis();
      LOG.info("PROFILE: Execute time elapsed : " + (endTime-startTime) + "ms");
    }
    
    return sResp;
  }

  /*
   * GetTypeInfo()
   *
   * Get information about types supported by the QueryCache.
   *
   */
  public TGetTypeInfoResp GetTypeInfo(TGetTypeInfoReq aReq) {
    return new TGetTypeInfoResp();
  }

  /*
   * GetCatalogs()
   *
   * Returns the list of databases
   * Result are ordered by TABLE_CATALOG
   *
   */
  public TGetCatalogsResp GetCatalogs(TGetCatalogsReq aReq) {
    return new TGetCatalogsResp();
  }

  /*
   * GetSchemas()
   *
   * Returns the schema names available in this database
   * The results are ordered by TABLE_CATALOG and TABLE_SCHEM
   *
   */
  public TGetSchemasResp GetSchemas(TGetSchemasReq aReq) {
    return new TGetSchemasResp();
  }

  /*
   * GetTables()
   *
   * Returns a list of tables with catalog, schema, and table type information.
   * Results are ordered by TABLE_TYPE, TABLE_CAT, TABLE_SCHEM, and TABLE_NAME
   *
   */
  public TGetTablesResp GetTables(TGetTablesReq aReq) {
    return new TGetTablesResp();
  }

  /*
   * GetTableTypes()
   *
   * Returns the table types available in this database.
   * The results are ordered by table type.
   * 
   */
  public TGetTableTypesResp GetTableTypes(TGetTableTypesReq aReq) {
    return new TGetTableTypesResp();
  }

  /*
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

  /*
   * GetOperationStatus()
   *
   * Get the status of an operation running on the server.
   * 
   */
  public TGetOperationStatusResp GetOperationStatus(TGetOperationStatusReq aReq) {
    return new TGetOperationStatusResp();
  }

  /*
   * CancelOperation()
   *
   * Cancels processing on the specified operation handle and
   * frees any resources which were allocated.
   *
   */
  public TCancelOperationResp CancelOperation(TCancelOperationReq aReq) {
    return new TCancelOperationResp();
  }

  /*
   * CloseOperation()
   *
   * This will free all of the resources which allocated on
   * the server to service the operation.
   * 
   */
  public TCloseOperationResp CloseOperation(TCloseOperationReq aReq) {
    LOG.info("CloseOperation is requested.");
    long startTime;
    long endTime;
    
    if (Configure.gQueryProfile) {
      startTime = System.currentTimeMillis();
    }
    TCloseOperationResp sResp = new TCloseOperationResp();
    sResp.setStatus(new TStatus());
    
    // 1. find statement and remove from statement UsingMap
    long sConnId = aReq.operationHandle.operationId.connid;
    long sStmtId = aReq.operationHandle.operationId.stmtid;
    ConnType sConnType = convertSesstypeToConntype(aReq.operationHandle.operationId.driverType);
    
    ConnNode sConn = gConnMgr.getConn(sConnType, sConnId);
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
      sConn.closeStmt(sStmtId);
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("CloseOperation error :" + e.getMessage(), e);
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.sqlState = e.getSQLState();
      sResp.status.errorCode = e.getErrorCode();
      sResp.status.errorMessage = e.getMessage();
      return sResp;
    }
    if (Configure.gQueryProfile) {
      endTime = System.currentTimeMillis();
      LOG.info("PROFILE: CloseOp time elapsed : " + (endTime-startTime) + "ms");
    }
    return sResp;
  }

  /*
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
    LOG.info("GetResultSetMetadata is requested.");
    long startTime;
    long endTime;
    
    if (Configure.gQueryProfile) {
      startTime = System.currentTimeMillis();
    }
    TGetResultSetMetadataResp sResp = new TGetResultSetMetadataResp();
    sResp.setStatus(new TStatus());
    
    //
    // 1. get ResultSetMetaData from ResultSet
    //
    long sConnId = aReq.operationHandle.operationId.connid;
    long sStmtId = aReq.operationHandle.operationId.stmtid;
    ConnType sConnType = convertSesstypeToConntype(aReq.operationHandle.operationId.driverType);
    
    ConnNode sConn = gConnMgr.getConn(sConnType, sConnId);
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
      LOG.error("GetResultSetMetadata error :" + e.getMessage(), e);
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.sqlState = e.getSQLState();
      sResp.status.errorCode = e.getErrorCode();
      sResp.status.errorMessage = e.getMessage();
      return sResp;
    }

    if (Configure.gQueryProfile) {
      endTime = System.currentTimeMillis();
      LOG.info("PROFILE: GetResultSetMetadata time elapsed : " + (endTime-startTime) + "ms");
    }
    return sResp;
  }

  /*
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
    LOG.info("FetchResults is requested.");
    long startTime;
    long endTime;
    
    if (Configure.gQueryProfile) {
      startTime = System.currentTimeMillis();
    }
    
    TFetchResultsResp sResp = new TFetchResultsResp();
    sResp.setStatus(new TStatus());
    
    //
    // 1. get ResultSet
    //
    long sConnId = aReq.operationHandle.operationId.connid;
    long sStmtId = aReq.operationHandle.operationId.stmtid;
    ConnType sConnType = convertSesstypeToConntype(aReq.operationHandle.operationId.driverType);
    
    ConnNode sConn = gConnMgr.getConn(sConnType, sConnId);
    if (sConn == null) {
      // the connection doesn't even exist, just send a error msg.
      LOG.error("FetchResults error : the connection doesn't exist." +
        " (id:" + sConnId + ")");
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.errorMessage = "FetchResults error : the connection doesn't exist.";
      return sResp;
    }
    
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
    
    //
    // 2. Fetch each row and save to TRowSet
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
      sRowSet.startRowOffset = 0;
      sStmt.sState = StmtNode.State.FETCHING;
      
      int sColCnt = sMeta.getColumnCount();
      // TODO: Below code block should be more optimized for performance
      long sRowCount = 0;
      while (sRS.next()) {
        ++sRowCount;
        TRow sRow = (TRow)gObjPool.getObject(TargetObjs.TROW);
        if (sRow == null) {
          sRow = new TRow();
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
              if (value != null) {
                TStringValue sStrV = (TStringValue)gObjPool.getObject(TargetObjs.TSTRINGVALUE);
                if (sStrV != null) {
                  sCell.setStringVal(sStrV.setValue(value));
                } else {
                  sCell.setStringVal(new TStringValue().setValue(value));
                }
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
      }

      sResp.hasMoreRows = false;
      sResp.results = sRowSet;
      sResp.status.statusCode = TStatusCode.SUCCESS_STATUS;
    } catch (SQLException e) {
      LOG.error("FetchResults error :" + e.getMessage(), e);
      sResp.status.statusCode = TStatusCode.ERROR_STATUS;
      sResp.status.sqlState = e.getSQLState();
      sResp.status.errorCode = e.getErrorCode();
      sResp.status.errorMessage = e.getMessage();
      return sResp;
    }
    if (Configure.gQueryProfile) {
      endTime = System.currentTimeMillis();
      LOG.info("PROFILE: Fetch time elapsed : " + (endTime-startTime) + "ms");
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
  }
}
