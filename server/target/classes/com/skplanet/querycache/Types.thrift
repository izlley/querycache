
namespace java com.skplanet.querycache.thrift

enum TTypeId {
  UNKNOWN,
  UNSUPPORTED,
  BOOLEAN,
  TINYINT,
  SMALLINT,
  INT,
  BIGINT,
  FLOAT,
  DOUBLE,
  DATE,
  DATETIME,
  TIMESTAMP,
  STRING,
  BINARY,
  DECIMAL,
  CHAR,
  ARRAY,
  MAP,
  STRUCT,
  UNION,
  USER_DEFINED,
}

const set<TTypeId> PRIMITIVE_TYPES = [
  TTypeId.BOOLEAN
  TTypeId.TINYINT
  TTypeId.SMALLINT
  TTypeId.INT
  TTypeId.BIGINT
  TTypeId.FLOAT
  TTypeId.DOUBLE
  TTypeId.STRING
  TTypeId.DATE
  TTypeId.DATETIME
  TTypeId.TIMESTAMP
  TTypeId.BINARY
  TTypeId.CHAR
  TTypeId.DECIMAL
]

const set<TTypeId> COMPLEX_TYPES = [
  TTypeId.ARRAY
  TTypeId.MAP
  TTypeId.STRUCT
  TTypeId.UNION
  TTypeId.USER_DEFINED
]

const set<TTypeId> COLLECTION_TYPES = [
  TTypeId.ARRAY
  TTypeId.MAP
]

const map<TTypeId,string> TYPE_NAMES = {
  TTypeId.BOOLEAN: "BOOLEAN"
  TTypeId.TINYINT: "TINYINT"
  TTypeId.SMALLINT: "SMALLINT"
  TTypeId.INT: "INT"
  TTypeId.BIGINT: "BIGINT"
  TTypeId.FLOAT: "FLOAT"
  TTypeId.DOUBLE: "DOUBLE"
  TTypeId.STRING: "STRING"
  TTypeId.DATE: "DATE"
  TTypeId.DATETIME: "DATETIME"
  TTypeId.TIMESTAMP: "TIMESTAMP"
  TTypeId.BINARY: "BINARY"
  TTypeId.CHAR: "CHAR"
  TTypeId.DECIMAL: "DECIMAL"
  TTypeId.ARRAY: "ARRAY"
  TTypeId.MAP: "MAP"
  TTypeId.STRUCT: "STRUCT"
  TTypeId.UNION: "UNION"
}

typedef i32 TTypeEntryPtr

// Type entry for a primitive type.
struct TPrimitiveTypeEntry {
  // The primitive type token. This must satisfy the condition
  // that type is in the PRIMITIVE_TYPES set.
  1: required TTypeId type

  // Only set if type == CHAR
  2: optional i32 len

  // Only set if type == DECIMAL
  3: optional i32 position
  4: optional i32 scale
}

// Type entry for an ARRAY type.
struct TArrayTypeEntry {
  1: required TTypeEntryPtr objectTypePtr
}

// Type entry for a MAP type.
struct TMapTypeEntry {
  1: required TTypeEntryPtr keyTypePtr
  2: required TTypeEntryPtr valueTypePtr
}

// Type entry for a STRUCT type.
struct TStructTypeEntry {
  1: required map<string, TTypeEntryPtr> nameToTypePtr
}

// Type entry for a UNIONTYPE type.
struct TUnionTypeEntry {
  1: required map<string, TTypeEntryPtr> nameToTypePtr
}

struct TUserDefinedTypeEntry {
  // The fully qualified name of the class implementing this type.
  1: required string typeClassName
}

// We use a union here since Thrift does not support inheritance.
union TTypeEntry {
  1: TPrimitiveTypeEntry primitiveEntry
  2: TArrayTypeEntry arrayEntry
  3: TMapTypeEntry mapEntry
  4: TStructTypeEntry structEntry
  5: TUnionTypeEntry unionEntry
  6: TUserDefinedTypeEntry userDefinedTypeEntry
}

// Type descriptor for columns.
struct TTypeDesc {
  // The "top" type is always the first element of the list.
  // If the top type is an ARRAY, MAP, STRUCT, or UNIONTYPE
  // type, then subsequent elements represent nested types.
  1: required list<TTypeEntry> types
}

/*
struct TColumnType {
  1: required TTypeId type

  // Only set if type == CHAR
  2: optional i32 len

  // Only set if type == DECIMAL
  3: optional i32 position
  4: optional i32 scale
}
*/
