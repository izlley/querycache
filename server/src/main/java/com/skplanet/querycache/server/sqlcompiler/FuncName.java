package com.skplanet.querycache.server.sqlcompiler;

public class FuncName {
  // TODO: store datastore name
  private String ds;
  private final String db;
  private final String func;
  
  public FuncName(String aDb, String aFunc) {
    if (aDb == null || aDb.isEmpty()) {
      this.db = "default";
    } else {
      this.db = aDb;
    }
    this.func = aFunc;
  }
  
  public String getDb() { return db; }
  public String getFunc() { return func; }
  public boolean isEmpty() { return func.isEmpty(); }
  public String toSQL() {
    if (db != null) {
      return db + "." + func;
    }
    return func;
  }
}

