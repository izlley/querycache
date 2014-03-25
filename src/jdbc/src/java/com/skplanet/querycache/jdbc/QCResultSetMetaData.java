package com.skplanet.querycache.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * QCResultSetMetaData.
 *
 */
public class QCResultSetMetaData implements java.sql.ResultSetMetaData {
  private final List<String> columnNames;
  private final List<String> columnTypes;
  
  enum SQLType {
    STRING_TYPE_NAME("string"),
    VOID_TYPE_NAME("void"),
    BOOLEAN_TYPE_NAME("boolean"),
    TINYINT_TYPE_NAME("tinyint"),
    SMALLINT_TYPE_NAME("smallint"),
    INT_TYPE_NAME("int"),
    BIGINT_TYPE_NAME("bigint"),
    FLOAT_TYPE_NAME("float"),
    DOUBLE_TYPE_NAME("double"),
    DATE_TYPE_NAME("date"),
    DATETIME_TYPE_NAME("datetime"),
    TIMESTAMP_TYPE_NAME("timestamp"),
    DECIMAL_TYPE_NAME("decimal"),
    BINARY_TYPE_NAME("binary");
    
    public final String name;
    SQLType(String name) {
      this.name = name;
    }
  }

  public QCResultSetMetaData(List<String> columnNames,
      List<String> columnTypes) {
    this.columnNames = columnNames;
    this.columnTypes = columnTypes;
  }

  public String getCatalogName(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public String getColumnClassName(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public int getColumnCount() throws SQLException {
    return columnNames.size();
  }

  public int getColumnDisplaySize(int column) throws SQLException {
    int columnType = getColumnType(column);

    return JdbcColumn.columnDisplaySize(columnType);
  }

  public String getColumnLabel(int column) throws SQLException {
    return columnNames.get(column - 1);
  }

  public String getColumnName(int column) throws SQLException {
    return columnNames.get(column - 1);
  }

  public int getColumnType(int column) throws SQLException {
    if (columnTypes == null) {
      throw new SQLException(
          "Could not determine column type name for ResultSet");
    }

    if (column < 1 || column > columnTypes.size()) {
      throw new SQLException("Invalid column value: " + column);
    }

    // we need to convert the thrift type to the SQL type
    String type = columnTypes.get(column - 1);

    // we need to convert the thrift type to the SQL type
    return Utils.hiveTypeToSqlType(type);
  }

  public String getColumnTypeName(int column) throws SQLException {
    if (columnTypes == null) {
      throw new SQLException(
          "Could not determine column type name for ResultSet");
    }

    if (column < 1 || column > columnTypes.size()) {
      throw new SQLException("Invalid column value: " + column);
    }

    // we need to convert the Hive type to the SQL type name
    // TODO: this would be better handled in an enum
    String type = columnTypes.get(column - 1);
    if ("string".equalsIgnoreCase(type)) {
      return SQLType.STRING_TYPE_NAME.name();
    } else if ("float".equalsIgnoreCase(type)) {
      return SQLType.FLOAT_TYPE_NAME.name();
    } else if ("double".equalsIgnoreCase(type)) {
      return SQLType.DOUBLE_TYPE_NAME.name();
    } else if ("boolean".equalsIgnoreCase(type)) {
      return SQLType.BOOLEAN_TYPE_NAME.name();
    } else if ("tinyint".equalsIgnoreCase(type)) {
      return SQLType.TINYINT_TYPE_NAME.name();
    } else if ("smallint".equalsIgnoreCase(type)) {
      return SQLType.SMALLINT_TYPE_NAME.name();
    } else if ("int".equalsIgnoreCase(type)) {
      return SQLType.INT_TYPE_NAME.name();
    } else if ("bigint".equalsIgnoreCase(type)) {
      return SQLType.BIGINT_TYPE_NAME.name();
    } else if ("timestamp".equalsIgnoreCase(type)) {
      return SQLType.TIMESTAMP_TYPE_NAME.name();
    } else if ("decimal".equalsIgnoreCase(type)) {
      return SQLType.DECIMAL_TYPE_NAME.name();
    } else if ("binary".equalsIgnoreCase(type)) {
      return SQLType.BINARY_TYPE_NAME.name();
    } else if (type.startsWith("map<")) {
      return SQLType.STRING_TYPE_NAME.name();
    } else if (type.startsWith("array<")) {
      return SQLType.STRING_TYPE_NAME.name();
    } else if (type.startsWith("struct<")) {
      return SQLType.STRING_TYPE_NAME.name();
    }

    throw new SQLException("Unrecognized column type: " + type);
  }

  public int getPrecision(int column) throws SQLException {
    int columnType = getColumnType(column);

    return JdbcColumn.columnPrecision(columnType);
  }

  public int getScale(int column) throws SQLException {
    int columnType = getColumnType(column);

    return JdbcColumn.columnScale(columnType);
  }

  public String getSchemaName(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public String getTableName(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isAutoIncrement(int column) throws SQLException {
    // Hive doesn't have an auto-increment concept
    return false;
  }

  public boolean isCaseSensitive(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isCurrency(int column) throws SQLException {
    // Hive doesn't support a currency type
    return false;
  }

  public boolean isDefinitelyWritable(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public int isNullable(int column) throws SQLException {
    // Hive doesn't have the concept of not-null
    return ResultSetMetaData.columnNullable;
  }

  public boolean isReadOnly(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isSearchable(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isSigned(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isWritable(int column) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new SQLException("Method not supported");
  }

  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException("Method not supported");
  }

}
