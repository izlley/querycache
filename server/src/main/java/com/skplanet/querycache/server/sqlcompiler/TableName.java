package com.skplanet.querycache.server.sqlcompiler;

public class TableName {
  // TODO: store datastore name
  private String ds;
  private final String db;
  private final String table;
  
  public TableName(String aDb, String aTbl) {
    if (aDb == null || aDb.isEmpty()) {
      this.db = "default";
    } else {
      this.db = aDb;
    }
    this.table = aTbl;
  }
  
  public String getDb() { return db; }
  public String getTable() { return table; }
  public boolean isEmpty() { return table.isEmpty(); }
  public String toSQL() {
    if (db != null) {
      return db + "." + table;
    }
    return table;
  }
}
