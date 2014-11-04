namespace java com.skplanet.querycache.thrift

include "Types.thrift"

struct TColumnDesc {
  1: required string columnName

  //2: required Types.TColumnType columnType
  2: required Types.TTypeDesc typeDesc

  // Ordinal position in the source table
  3: required i32 position

  4: optional string comment

  // Stats for this table, if any are available
  // 5: optional TColumnStats col_stats
}
