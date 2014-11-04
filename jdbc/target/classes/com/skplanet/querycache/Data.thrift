
namespace java com.skplanet.querycache.thrift

include "Types.thrift"
include "MetaData.thrift"

/*
 * Plan1 : rebuilding resultset
 */
/*
// A Boolean column value.
struct TBoolValue {
  // NULL if value is unset.
  1: optional bool value
}

// A Byte column value.
struct TByteValue {
  // NULL if value is unset.
  1: optional byte value
}

// A signed, 16 bit column value.
struct TI16Value {
  // NULL if value is unset
  1: optional i16 value
}

// A signed, 32 bit column value
struct TI32Value {
  // NULL if value is unset
  1: optional i32 value
}

// A signed 64 bit column value
struct TI64Value {
  // NULL if value is unset
  1: optional i64 value
}

// A floating point 64 bit column value
struct TDoubleValue {
  // NULL if value is unset
  1: optional double value
}
*/

struct TStringValue {
  // NULL if value is unset
  1: optional string value
}

// this is a union over all possible return types
union TColumnValue {
  1: bool   boolVal      // BOOLEAN
  2: byte   byteVal      // TINYINT
  3: i16    i16Val       // SMALLINT
  4: i32    i32Val       // INT
  5: i64    i64Val       // BIGINT, TIMESTAMP
  6: double doubleVal  // FLOAT, DOUBLE
  // string or any binary column data can be stored
  7: TStringValue stringVal  // STRING, CHAR, LIST, MAP, STRUCT, DECIMAL, BINARY
}

struct TRow {
  1: list<TColumnValue> colVals
}

struct TTableSchema {
  1: required list<MetaData.TColumnDesc> columns
}

// List of rows and metadata describing their columns
struct TRowSet {
  // 0-based
  1: required i64 startRowOffset
  2: required list<TRow> rows
  3: optional TTableSchema schema
}

/*
 * Plan2 : rebuilding resultset efficiently
 */

/*
struct TRowSet {
  // total number of rows contained in this batch
  1: required i32 num_rows

  2: required i32 num_cols

  // there are a total number of num_rows x num_cols offsets 
  3: list<i32> col_offsets

  // binary row data
  4: string row_data 

  // Indicates whether row_data is snappy-compressed
  5: bool is_compressed
}
*/

/*
 * Plan3 : just serialize jdbc ResultSet
 */

/*
struct TRowSet {
  // Serialized ResultSet object
  1: required string object
}
*/
