package com.skplanet.querycache.server.sqlcompiler;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.skplanet.querycache.server.auth.Privilege;

public class QueryStmt {
  QueryType type = QueryType.OTHERS;
  boolean isLimit = false;
  boolean isFilterCond = false;
  boolean isNested = false;
  boolean isJoin = false;
  boolean isWith = false;
  // for impala
  boolean isInvalidateAll = false;
  short   nestedLvl = 0;
  short   unionLvl = 0;
  
  List<Map.Entry<String, Privilege>> dataStoreRefs;
  List<Map.Entry<DatabaseName, Privilege>> databaseRefs;
  // tableRefs list can contain table names and view names
  List<Map.Entry<TableName, Privilege>> tableRefs;
  // viewRefs list can only contain view names
  List<Map.Entry<TableName, Privilege>> viewRefs;
  // to recognize subquery factoring aliases
  List<String> withRefs;
  List<Map.Entry<FuncName, Privilege>> functionRefs;
  List<Map.Entry<String, Privilege>> uriPathRefs;
  //ArrayList<> slotRefs;
  
  static public enum QueryType {
    SELECT,
    INSERT,
    UPDATE,
    DELETE,
    TRUNCATE,
    DROP,
    ALTER,
    CREATE,
    EXPLAIN,
    SHOW,
    DESCRIBE,
    UNION,
    // =for impala= //
    REFRESH,
    INVALIDATE,
    // ============ //
    USE,
    COMPUTE_STATS,
    LOAD,
    OTHERS
  }
  
  public QueryStmt() {
    this.dataStoreRefs = new ArrayList<Map.Entry<String, Privilege>>();
    this.databaseRefs = new ArrayList<Map.Entry<DatabaseName, Privilege>>();
    this.tableRefs = new ArrayList<Map.Entry<TableName, Privilege>>();
    this.viewRefs = new ArrayList<Map.Entry<TableName, Privilege>>();
    this.functionRefs = new ArrayList<Map.Entry<FuncName, Privilege>>();
    this.uriPathRefs = new ArrayList<Map.Entry<String, Privilege>>();
    this.withRefs = new ArrayList<String>();
  }
  
  public String toStringdatabaseRefs() {
    String dblist = "";
    for (Map.Entry<DatabaseName, Privilege> db : databaseRefs) {
      dblist += db.getKey().toSQL()+ "[" + db.getValue().toString() + "] ";
    }
    return dblist;
  }
  
  public String toStringtableRefs() {
    String tablelist = "";
    for (Map.Entry<TableName, Privilege> tbl : tableRefs) {
      tablelist += tbl.getKey().toSQL()+ "[" + tbl.getValue().toString() + "] ";
    }
    return tablelist;
  }
  
  public String toStringviewRefs() {
    String viewlist = "";
    for (Map.Entry<TableName, Privilege> vw : viewRefs) {
      viewlist += vw.getKey().toSQL() + "[" + vw.getValue().toString() + "] ";
    }
    return viewlist;
  }
  
  public String toStringfunctionRefs() {
    String funclist = "";
    for (Map.Entry<FuncName, Privilege> func : functionRefs) {
      funclist += func.getKey().toSQL() + "[" + func.getValue().toString() + "] ";
    }
    
    return funclist;
  }
}
