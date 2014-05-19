
namespace java com.skplanet.querycache.cli.thrift

include "Types.thrift"
include "Data.thrift"
include "MetaData.thrift"

enum TProtocolVersion {
  QUERYCACHE_CLI_PROTOCOL_V1,
}

enum TStatusCode {
  SUCCESS_STATUS,
  SUCCESS_WITH_INFO_STATUS,
  STILL_EXECUTING_STATUS,
  ERROR_STATUS,
  INVALID_HANDLE_STATUS
}

struct TStatus {
  1: required TStatusCode statusCode
  2: optional list<string> infoMessages
  3: optional string sqlState
  4: optional i32 errorCode
  5: optional string errorMessage
}

enum TOperationState {
  // The operation has been initialized
  INITIALIZED_STATE,

  // The operation is running. In this state the result set is not available
  RUNNING_STATE,

  // The operation has completed. the result set may be fetched.
  FINISHED_STATE,

  //CANCELED_STATE,

  // The operation was closed by a client
  CLOSED_STATE,

  ERROR_STATE,

  UNKNOWN_STATE,
}

enum TOperationType {
  EXECUTE_STATEMENT,
  GET_TYPE_INFO,
  GET_CATALOGS,
  GET_SCHEMAS,
  GET_TABLES,
  GET_TABLE_TYPES,
  GET_COLUMNS,
  UNKNOWN,
}

/*
enum TSessionType {
  SESS_IMPALA_JDBC,
  SESS_HIVE_JDBC,
  SESS_PHOENIX_JDBC,
  SESS_IMPALA_THRIFT,
  SESS_HBASE,
  SESS_MYSQL_JDBC,
  SESS_SPARK_JDBC,
  SESS_ORACLE_JDBC,
  SESS_DB2_JDBC,
  SESS_BERKELEY_JDBC,
  SESS_OTHERS,
}
*/

struct THandleIdentifier {
  1: required i64 connid = 0
  2: required i64 stmtid = 0
  // Heterogeneous storage system can be supported for QC
  3: required string driverType
}

struct TSessionHandle {
  1: required THandleIdentifier sessionId
}

struct TOperationHandle {
  1: required THandleIdentifier operationId
  2: required TOperationType operationType

  3: required bool hasResultSet

  4: optional double updateRowCount
}

struct THostInfo {
  1: required string hostname
  2: required string ipaddr
  3: required i32 portnum
}

/*
 * OpenSession()
 *
 *
 * Open a session (connection) on the server against which operations may be executed
 */
struct TOpenSessionReq {
  1: required TProtocolVersion clientProtocol = TProtocolVersion.QUERYCACHE_CLI_PROTOCOL_V1
  // to recognize storage type '<protocol_type>:<service_type>'
  2: required string url
  // for authentication
  3: optional string username
  4: optional string password 

  5: optional map<string, string> configuration
}

struct TOpenSessionResp {
  1: required TStatus status  
  2: required TProtocolVersion serverProtocol = TProtocolVersion.QUERYCACHE_CLI_PROTOCOL_V1
  3: required TSessionHandle sessionHandle
  4: optional THostInfo hostInfo
  5: optional map<string, string> configuration 
}

/*
 * CloseSession()
 *
 * Closes the specified session and frees any resources currently allocated 
 * to that session. Any open operations in that session will be closed.
 */
struct TCloseSessionReq {
  1: required TSessionHandle sessionHandle
}

struct TCloseSessionResp {
  1: required TStatus status
}

enum TGetInfoType {
  CLI_SOME_REQ
}

union TGetInfoValue {
  1: string stringValue
  2: i16 smallIntValue
  3: i32 integerBitmask
  4: i32 integerFlag
  5: i32 binaryValue
  6: i64 lenValue
}

/*
 * GetInfo()
 *
 * This function is based on ODBC's CLIGetInfo() function.
 *
 */
struct TGetInfoReq {
  1: required TSessionHandle sessionHandle

  2: required TGetInfoType infoType
}

struct TGetInfoResp {
  1: required TStatus status

  2: required TGetInfoValue infoValue
}

 /*
  * ExecuteStatement()
  *
  * Execute a statement. 
  *
  */

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

 /*
  * GetTypeInfo()
  *
  * Get information about types supported by the QueryCache.
  *
  */
struct TGetTypeInfoReq {
  1: required TSessionHandle sessionHandle
}

struct TGetTypeInfoResp {
  1: required TStatus status
  2: optional TOperationHandle operationHandle
}

 /*
  * GetCatalogs()
  *
  * Returns the list of databases
  * Result are ordered by TABLE_CATALOG
  *
  */
struct TGetCatalogsReq {
  1: required TSessionHandle sessionHandle
}

struct TGetCatalogsResp {
 1: required TStatus status
 2: optional TOperationHandle operationHandle
}

/*
 * GetSchemas()
 *
 * Returns the schema names available in this database
 * The results are ordered by TABLE_CATALOG and TABLE_SCHEM
 *
 */
struct TGetSchemasReq {
  1: required TSessionHandle sessionHandle

  2: optional string catalogName

  3: optional string schemaName
}

struct TGetSchemasResp {
  1: required TStatus status
  2: optional TOperationHandle operationHandle
}

/*
 * GetTables()
 *
 * Returns a list of tables with catalog, schema, and table type information.
 * Results are ordered by TABLE_TYPE, TABLE_CAT, TABLE_SCHEM, and TABLE_NAME
 *
 */
struct TGetTablesReq {
  1: required TSessionHandle sessionHandle
  2: optional string catalogName
  3: optional string schemaName
  4: optional string tableName
  5: optional list<string> tableTypes
}

struct TGetTablesResp {
  1: required TStatus status
  2: optional TOperationHandle operationHandle
}

/*
 * GetTableTypes()
 *
 * Returns the table types available in this database.
 * The results are ordered by table type.
 */
struct TGetTableTypesReq {
  1: required TSessionHandle sessionHandle
}

struct TGetTableTypesResp {
  1: required TStatus status
  2: optional TOperationHandle operationHandle
}

/*
 * GetColumns()
 *
 * Returns a list of columns in the specified tables.
 * Results are ordered by TABLE_CAT, TABLE_SCHEM, TABLE_NAME, and
 * ORDINAL_POSITION
 */
struct TGetColumnsReq {
  1: required TSessionHandle sessionHandle
  2: optional string catalogName
  3: optional string schemaName
  4: optional string tableName
  5: optional string columnName
}

struct TGetColumnsResp {
  1: required TStatus status
  2: optional TOperationHandle operationHandle
}

/*
 * GetOperationStatus()
 *
 * Get the status of an operation running on the server.
 */
struct TGetOperationStatusReq {
  1: required TOperationHandle operatioinHandle
}

struct TGetOperationStatusResp {
  1: required TStatus status
  2: optional TOperationState operationState
}

/*
 * CancelOperation()
 *
 * Cancels processing on the specified operation handle and
 * frees any resources which were allocated.
 *
 */
struct TCancelOperationReq {
  1: required TOperationHandle operationHandle
}

struct TCancelOperationResp {
  1: required TStatus status
}

/*
 * CloseOperation()
 *
 * This will free all of the resources which allocated on
 * the server to service the operation.
 */
struct TCloseOperationReq {
  1: required TOperationHandle operationHandle
}

struct TCloseOperationResp {
  1: required TStatus status
}

/*
 * GetResultSetMetadata()
 *
 * Retrieves schema information for the specified operation
 *
 */
struct TGetResultSetMetadataReq {
  1: required TOperationHandle operationHandle
}

struct TGetResultSetMetadataResp {
  1: required TStatus status
  2: optional Data.TTableSchema schema
}

enum TFetchOrientation {
  FETCH_NEXT,
  FETCH_PRIOR,
  FETCH_RELATIVE,
  FETCH_ABSOLUTE,
  FETCH_FIRST,
  FETCH_LAST
}

/*
 * FetchResults()
 *
 * Fetch rows from server corresponding to a particular OperationHandle
 *
 */
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

service TCLIService {
  TOpenSessionResp OpenSession(1:TOpenSessionReq req);

  TCloseSessionResp CloseSession(1:TCloseSessionReq req);

  TGetInfoResp GetInfo(1:TGetInfoReq req);

  TExecuteStatementResp ExecuteStatement(1:TExecuteStatementReq req);

  TGetTypeInfoResp GetTypeInfo(1:TGetTypeInfoReq req);

  TGetCatalogsResp GetCatalogs(1:TGetCatalogsReq req);

  TGetSchemasResp GetSchemas(1:TGetSchemasReq req);

  TGetTablesResp GetTables(1:TGetTablesReq req);

  TGetTableTypesResp GetTableTypes(1:TGetTableTypesReq req);

  TGetColumnsResp GetColumns(1:TGetColumnsReq req);

  TGetOperationStatusResp GetOperationStatus(1:TGetOperationStatusReq req);

  TCancelOperationResp CancelOperation(1:TCancelOperationReq req);

  TCloseOperationResp CloseOperation(1:TCloseOperationReq req);

  TGetResultSetMetadataResp GetResultSetMetadata(1:TGetResultSetMetadataReq req);

  TFetchResultsResp FetchResults(1:TFetchResultsReq req);
}

