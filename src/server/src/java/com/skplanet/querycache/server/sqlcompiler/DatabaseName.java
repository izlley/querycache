package com.skplanet.querycache.server.sqlcompiler;

public class DatabaseName{
  // TODO: store datastore name
  private String ds;
  private final String db;
  
  public DatabaseName(String aDs, String aDb) {
    this.ds = aDs;
    this.db = aDb;
  }
  
  public String getDs() { return ds; }
  public String getDb() { return db; }
  public boolean isEmpty() { return db.isEmpty(); }
  public String toSQL() {
    //if (ds != null) {
    //  return ds + "." + db;
    //}
    return db;
  }
}