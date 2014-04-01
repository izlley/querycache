package com.skplanet.querycache.service.cli;

import java.sql.DatabaseMetaData;

import com.skplanet.querycache.thrift.TTypeId;

/**
 * Type.
 *
 */
public enum Type {
  CHAR_TYPE("CHAR",
      java.sql.Types.CHAR,
      TTypeId.CHAR),
  BOOLEAN_TYPE("BOOLEAN",
      java.sql.Types.BOOLEAN,
      TTypeId.BOOLEAN),
  TINYINT_TYPE("TINYINT",
      java.sql.Types.TINYINT,
      TTypeId.TINYINT),
  SMALLINT_TYPE("SMALLINT",
      java.sql.Types.SMALLINT,
      TTypeId.SMALLINT),
  INT_TYPE("INT",
      java.sql.Types.INTEGER,
      TTypeId.INT),
  BIGINT_TYPE("BIGINT",
      java.sql.Types.BIGINT,
      TTypeId.BIGINT),
  FLOAT_TYPE("FLOAT",
      java.sql.Types.FLOAT,
      TTypeId.FLOAT),
  DOUBLE_TYPE("DOUBLE",
      java.sql.Types.DOUBLE,
      TTypeId.DOUBLE),
  STRING_TYPE("STRING",
      java.sql.Types.VARCHAR,
      TTypeId.STRING),
  TIMESTAMP_TYPE("TIMESTAMP",
      java.sql.Types.TIMESTAMP,
      TTypeId.TIMESTAMP),
  BINARY_TYPE("BINARY",
      java.sql.Types.BINARY,
      TTypeId.BINARY),
  DECIMAL_TYPE("DECIMAL",
      java.sql.Types.DECIMAL,
      TTypeId.DECIMAL,
      false, false),
  ARRAY_TYPE("ARRAY",
      java.sql.Types.VARCHAR,
      TTypeId.STRING,
      true, true),
  MAP_TYPE("MAP",
      java.sql.Types.VARCHAR,
      TTypeId.STRING,
      true, true),
  STRUCT_TYPE("STRUCT",
      java.sql.Types.VARCHAR,
      TTypeId.STRING,
      true, false),
  UNION_TYPE("UNIONTYPE",
      java.sql.Types.VARCHAR,
      TTypeId.STRING,
      true, false),
  USER_DEFINED_TYPE(null,
      java.sql.Types.VARCHAR,
      TTypeId.STRING,
      true, false);

  private final String name;
  private final TTypeId tType;
  private final int javaSQLType;
  private final boolean isComplex;
  private final boolean isCollection;


  Type(String name, int javaSQLType, TTypeId tType, boolean isComplex, boolean isCollection) {
    this.name = name;
    this.javaSQLType = javaSQLType;
    this.tType = tType;
    this.isComplex = isComplex;
    this.isCollection = isCollection;
  }

  Type(String name, int javaSqlType, TTypeId tType) {
    this(name, javaSqlType, tType, false, false);
  }

  public boolean isPrimitiveType() {
    return !isComplex;
  }

  public boolean isComplexType() {
    return isComplex;
  }

  public boolean isCollectionType() {
    return isCollection;
  }

  public static Type getType(TTypeId tType) {
    for (Type type : values()) {
      if (tType.equals(type.tType)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unregonized Thrift TTypeId value: " + tType);
  }

  public static Type getType(String name) {
    for (Type type : values()) {
      if (name.equalsIgnoreCase(type.name)) {
        return type;
      } else if (type.isComplexType()) {
        if (name.toUpperCase().startsWith(type.name)) {
            return type;
        }
      }
    }
    throw new IllegalArgumentException("Unrecognized type name: " + name);
  }

  /**
   * Radix for this type (typically either 2 or 10)
   * Null is returned for data types where this is not applicable.
   */
  public Integer getNumPrecRadix() {
    switch (this) {
    case TINYINT_TYPE:
    case SMALLINT_TYPE:
    case INT_TYPE:
    case BIGINT_TYPE:
      return 10;
    case FLOAT_TYPE:
    case DOUBLE_TYPE:
      return 2;
    default:
      // everything else (including boolean and string) is null
      return null;
    }
  }

  /**
   * The number of fractional digits for this type.
   * Null is returned for data types where this is not applicable.
   */
  public Integer getDecimalDigits() {
    switch (this) {
    case BOOLEAN_TYPE:
    case TINYINT_TYPE:
    case SMALLINT_TYPE:
    case INT_TYPE:
    case BIGINT_TYPE:
      return 0;
    case FLOAT_TYPE:
      return 7;
    case DOUBLE_TYPE:
      return 15;
    default:
      return null;
    }
  }

  /**
   * Maximum precision for numeric types.
   * Returns null for non-numeric types.
   * @return
   */
  public Integer getPrecision() {
    switch (this) {
    case TINYINT_TYPE:
      return 3;
    case SMALLINT_TYPE:
      return 5;
    case INT_TYPE:
      return 10;
    case BIGINT_TYPE:
      return 19;
    case FLOAT_TYPE:
      return 7;
    case DOUBLE_TYPE:
      return 15;
    default:
      return null;
    }
  }

  /**
   * Scale for this type.
   */
  public Integer getScale() {
    switch (this) {
    case BOOLEAN_TYPE:
    case STRING_TYPE:
    case TIMESTAMP_TYPE:
    case TINYINT_TYPE:
    case SMALLINT_TYPE:
    case INT_TYPE:
    case BIGINT_TYPE:
      return 0;
    case FLOAT_TYPE:
      return 7;
    case DOUBLE_TYPE:
      return 15;
    case DECIMAL_TYPE:
      return Integer.MAX_VALUE;
    default:
      return null;
    }
  }

  /**
   * The column size for this type.
   * For numeric data this is the maximum precision.
   * For character data this is the length in characters.
   * For datetime types this is the length in characters of the String representation
   * (assuming the maximum allowed precision of the fractional seconds component).
   * For binary data this is the length in bytes.
   * Null is returned for for data types where the column size is not applicable.
   */
  public Integer getColumnSize() {
    if (isNumericType()) {
      return getPrecision();
    }
    switch (this) {
    case STRING_TYPE:
    case BINARY_TYPE:
      return Integer.MAX_VALUE;
    case TIMESTAMP_TYPE:
      return 30;
    default:
      return null;
    }
  }

  public boolean isNumericType() {
    switch (this) {
    case TINYINT_TYPE:
    case SMALLINT_TYPE:
    case INT_TYPE:
    case BIGINT_TYPE:
    case FLOAT_TYPE:
    case DOUBLE_TYPE:
    case DECIMAL_TYPE:
      return true;
    default:
      return false;
    }
  }

  /**
   * Prefix used to quote a literal of this type (may be null)
   */
  public String getLiteralPrefix() {
    return null;
  }

  /**
   * Suffix used to quote a literal of this type (may be null)
   * @return
   */
  public String getLiteralSuffix() {
    return null;
  }

  /**
   * Can you use NULL for this type?
   * @return
   * DatabaseMetaData.typeNoNulls - does not allow NULL values
   * DatabaseMetaData.typeNullable - allows NULL values
   * DatabaseMetaData.typeNullableUnknown - nullability unknown
   */
  public Short getNullable() {
    // All QC types are nullable
    return DatabaseMetaData.typeNullable;
  }

  /**
   * Is the type case sensitive?
   * @return
   */
  public Boolean isCaseSensitive() {
    switch (this) {
    case STRING_TYPE:
      return true;
    default:
      return false;
    }
  }

  /**
   * Parameters used in creating the type (may be null)
   * @return
   */
  public String getCreateParams() {
    return null;
  }

  /**
   * Can you use WHERE based on this type?
   * @return
   * DatabaseMetaData.typePredNone - No support
   * DatabaseMetaData.typePredChar - Only support with WHERE .. LIKE
   * DatabaseMetaData.typePredBasic - Supported except for WHERE .. LIKE
   * DatabaseMetaData.typeSearchable - Supported for all WHERE ..
   */
  public Short getSearchable() {
    if (isPrimitiveType()) {
      return DatabaseMetaData.typeSearchable;
    }
    return DatabaseMetaData.typePredNone;
  }

  /**
   * Is this type unsigned?
   * @return
   */
  public Boolean isUnsignedAttribute() {
    if (isNumericType()) {
      return false;
    }
    return true;
  }

  /**
   * Can this type represent money?
   * @return
   */
  public Boolean isFixedPrecScale() {
    return false;
  }

  /**
   * Can this type be used for an auto-increment value?
   * @return
   */
  public Boolean isAutoIncrement() {
    return false;
  }

  /**
   * Localized version of type name (may be null).
   * @return
   */
  public String getLocalizedName() {
    return null;
  }

  /**
   * Minimum scale supported for this type
   * @return
   */
  public Short getMinimumScale() {
    return 0;
  }

  /**
   * Maximum scale supported for this type
   * @return
   */
  public Short getMaximumScale() {
    return 0;
  }

  public TTypeId toTType() {
    return tType;
  }

  public int toJavaSQLType() {
    return javaSQLType;
  }

  public String getName() {
    return name;
  }
}
