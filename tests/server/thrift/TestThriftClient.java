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

package test.com.skplanet.querycache.jdbc;

import java.util.List;

import com.skplanet.querycache.thrift.*;
import com.skplanet.querycache.cli.thrift.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;

public class TestThriftClient {
  public static void main(String [] args) {
    /*if (args.length != 1) {
      System.out.println("Please enter 'simple' or 'secure'");
      System.exit(0);
    }*/

    try {
      TTransport transport;
      transport = new TSocket("localhost", 8282);
      transport.open();

      //TProtocol protocol = new  TBinaryProtocol(transport);
      TProtocol protocol = new TCompactProtocol(transport);
      TCLIService.Client client = new TCLIService.Client(protocol);

      perform(client);

      transport.close();
    } catch (TException x) {
      x.printStackTrace();
    }
  }

  private static void perform(TCLIService.Client client) throws TException
  {
    //
    // OpenSession Test
    //
    /*
    struct TOpenSessionReq {
      1: required TProtocolVersion clientProtocol = TProtocolVersion.QUERYCACHE_CLI_PROTOCOL_V1
      // to recognize storage type '<protocol_type>:<service_type>'
      2: required string url
      // for authentication
      3: optional string username
      4: optional string password

      5: optional map<string, string> configuration
    }
    */
    TOpenSessionReq sOpenReq = new TOpenSessionReq();
    sOpenReq.url = new String("jdbc:bdb");
    
    TOpenSessionResp sOpenResp = client.OpenSession(sOpenReq);
    
    long sConnId = sOpenResp.sessionHandle.sessionId.connid;
    System.out.println("OpenSess STATUE:"+sOpenResp.status.statusCode + ":" + sConnId );
    
    //
    // ExecuteStatement()
    //
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
    TExecuteStatementReq sExecReq = new TExecuteStatementReq();
    sExecReq.setSessionHandle(new TSessionHandle().setSessionId(
        new THandleIdentifier().setConnid(sConnId)));
    sExecReq.statement = "select * from MOCB limit 10";
    
    TExecuteStatementResp sExecResp = client.ExecuteStatement(sExecReq);
    long sConnIdExec = sExecResp.operationHandle.operationId.connid;
    long sStmtId = sExecResp.operationHandle.operationId.stmtid;
    System.out.println("Execute STATUE:"+sExecResp.status.statusCode + ":" + sConnId + "/" + sStmtId );
    System.out.println("OperationType:"+sExecResp.operationHandle.getOperationType()+
        ",hasResultSet:"+sExecResp.operationHandle.hasResultSet);
    
    //
    // GetResultSetMetadata()
    //
    /*
    struct TGetResultSetMetadataReq {
      1: required TOperationHandle operationHandle
    }

    struct TGetResultSetMetadataResp {
      1: required TStatus status
      2: optional Data.TTableSchema schema
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
    */
    
    TGetResultSetMetadataReq sRSMeta = new TGetResultSetMetadataReq();
    sRSMeta.setOperationHandle(sExecResp.getOperationHandle().deepCopy());
    
    TGetResultSetMetadataResp sRSMetaResp = client.GetResultSetMetadata(sRSMeta);
    
    System.out.println("GetRSMeta STATUE:"+sRSMetaResp.status.statusCode);
    List<TColumnDesc> sColsDesc = sRSMetaResp.getSchema().getColumns();
    for (int i = 0; i < sColsDesc.size(); i++) {
      TColumnDesc sColDesc = sColsDesc.get(i);
      System.out.println("COLNAME:"+sColDesc.columnName+", COLTYPE:"+sColDesc.getTypeDesc().
                         types.get(0).getPrimitiveEntry().getType()+
                         ", OFFSET:"+ sColDesc.position);
    }
    
    //
    // FetchResults()
    //
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
    TFetchResultsReq sFetchReq = new TFetchResultsReq();
    sFetchReq.setOperationHandle(sExecResp.getOperationHandle().deepCopy());
    TFetchResultsResp sFetchResp = client.FetchResults(sFetchReq);
    System.out.println("Fetch STATUE:"+sFetchResp.status.statusCode);
    System.out.println(",startOffset:"+sFetchResp.results.startRowOffset);
    for (int j = 0; j < sColsDesc.size(); j++) {
      TColumnDesc sColDesc = sColsDesc.get(j);
      System.out.printf(sColDesc.columnName+":"+sColDesc.getTypeDesc().
          types.get(0).getPrimitiveEntry().getType()+"  ");
    }
    for (int i = 0 ; i < sFetchResp.getResults().getRows().size() ; i++) {
      System.out.println();
      TRow sRow = sFetchResp.getResults().getRows().get(i);
      for (int j = 0 ; j < sRow.colVals.size() ; j++) {
        System.out.printf("%s  ", sRow.colVals.get(j));
      }
      System.out.println();
    }
    
    //
    // CloseOperation
    //
    /*
    struct TCloseOperationReq {
      1: required TOperationHandle operationHandle
    }

    struct TCloseOperationResp {
      1: required TStatus status
    }
    */
    TCloseOperationReq sCloseOpReq = new TCloseOperationReq();
    sCloseOpReq.setOperationHandle(sExecResp.getOperationHandle().deepCopy());
    
    TCloseOperationResp sCloseOpResp = client.CloseOperation(sCloseOpReq);
    
    System.out.println("CloseOp STATUE:"+sCloseOpResp.status.statusCode);
    
    //
    // CloseSession Test
    //
    /*
    struct TCloseSessionReq {
  1: required TSessionHandle sessionHandle
}

struct TCloseSessionResp {
  1: required TStatus status
}
    */
    TCloseSessionReq sCloseReq = new TCloseSessionReq();
    sCloseReq.setSessionHandle(new TSessionHandle().setSessionId(new THandleIdentifier().setConnid(sConnId)));
    
    TCloseSessionResp sCloseResp = client.CloseSession(sCloseReq);
    
    System.out.println("CloseSess STATUS:"+sCloseResp.status.statusCode);
  }
}
