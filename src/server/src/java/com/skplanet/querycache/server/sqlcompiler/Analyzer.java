package com.skplanet.querycache.server.sqlcompiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.AbstractMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.skplanet.querycache.server.ConnMgr;
import com.skplanet.querycache.server.auth.AuthorizationLoader;
import com.skplanet.querycache.server.auth.Privilege;
import com.skplanet.querycache.server.auth.PrivilegeRequestBuilder;
import com.skplanet.querycache.server.common.AnalyzerException;
import com.skplanet.querycache.server.auth.Privilege;

/**
 * Wrapper class for parser and analyzer.
 */
public class Analyzer {
  private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);
  private String stmt_;
  private final String user_;
  private final String connType_;
  private final ConnMgr connMgr_;
  
  private AnalysisResult analResult;
  
  public Analyzer(String user, String connType, ConnMgr connMgr) {
    this.user_ = user;
    this.connType_ = connType;
    this.connMgr_ = connMgr;
  }
  
  static public class AnalysisResult {
    private QueryStmt queryStmt_;
    
    public QueryStmt getQueryStmt() {
      return queryStmt_;
    }
  }
  
  public void analyzer(String stmt, boolean isAuthCheck) throws AuthorizationException, AnalyzerException {
    this.stmt_ = stmt;
    analResult = new AnalysisResult();
    
    SqlScanner input = new SqlScanner(new StringReader(stmt));
    SqlParser parser = new SqlParser(input);
    parser.datastore = connType_.split(":")[1];
    try {
      analResult.queryStmt_ = (QueryStmt) parser.parse().value;
      if (analResult.queryStmt_ == null) return;
      LOG.debug("User=" + user_ + ", QueryStmt: QueryType=" + analResult.queryStmt_.type.toString() +
             ", [limit,filter,nested,join,with,invalidateall]=[" + analResult.queryStmt_.isLimit+","+
             analResult.queryStmt_.isFilterCond+","+analResult.queryStmt_.isNested+","+analResult.queryStmt_.isJoin+
             ","+analResult.queryStmt_.isWith+","+analResult.queryStmt_.isInvalidateAll+"]"+
             ", [nestedLvl,unionLvl]="+"["+analResult.queryStmt_.nestedLvl+","+
             analResult.queryStmt_.unionLvl+"]"+
      		   ", Datastore=" + analResult.queryStmt_.dataStoreRefs.toString() +
             ", Database=[" + analResult.queryStmt_.toStringdatabaseRefs() +
             "], Table=[" + analResult.queryStmt_.toStringtableRefs() +
             "], View=[" + analResult.queryStmt_.toStringviewRefs() +
             "], With=" + analResult.queryStmt_.withRefs.toString() +
             ", Function=[" + analResult.queryStmt_.toStringfunctionRefs() +
             "], Uris=" + analResult.queryStmt_.uriPathRefs.toString());
      if (isAuthCheck) {
        checkAuthorization(analResult);
      }
    } catch (AuthorizationException e) {
      throw e;
    } catch (Exception e) { // includes syntax error
      String msg = parser.getErrorMsg(stmt);
      throw new AnalyzerException((msg!=null)? msg: e.getMessage(), e);
    }
  }
  
  void checkAuthorization(Analyzer.AnalysisResult analResult)
      throws AuthorizationException {
    Preconditions.checkNotNull(analResult.queryStmt_);
    
    PrivilegeRequestBuilder pb = new PrivilegeRequestBuilder();
    AuthorizationLoader authLoader = connMgr_.getAuthLoader(connType_);
    
    // 1. check Datastore privilege
    for (Map.Entry<String, Privilege> ds : analResult.queryStmt_.dataStoreRefs) {
      authLoader.checkAccess(user_, pb.onServer(ds.getKey()).allOf(ds.getValue()).toRequest());
    }
    pb.clear();
    // 2. check Database privilege
    for (Map.Entry<DatabaseName, Privilege> db : analResult.queryStmt_.databaseRefs) {
      authLoader.checkAccess(user_, pb.onDb(db.getKey().getDb())
        .allOf(db.getValue()).toRequest());
    }
    pb.clear();
    // 3. check Table privilege
    for (Map.Entry<TableName, Privilege> table : analResult.queryStmt_.tableRefs) {
      if (analResult.queryStmt_.withRefs.contains(table.getKey().getTable()) &&
          table.getKey().getDb().equals("default"))
        continue;
      authLoader.checkAccess(user_, pb.onTable(table.getKey().getDb(), table.getKey().getTable())
        .allOf(table.getValue()).toRequest());
    }
    pb.clear();
    // 4. check View privilege
    for (Map.Entry<TableName, Privilege> vw : analResult.queryStmt_.viewRefs) {
      authLoader.checkAccess(user_, pb.onTable(vw.getKey().getDb(), vw.getKey().getTable())
        .allOf(vw.getValue()).toRequest());
    }
    pb.clear();
    // 5. check Function privilege
    for (Map.Entry<FuncName, Privilege> func : analResult.queryStmt_.functionRefs) {
      if (!authLoader.isUDFExist(func.getKey().getFunc()))
        continue;
      authLoader.checkAccess(user_, pb.onFunction(func.getKey().getDb(), func.getKey().getFunc())
        .allOf(func.getValue()).toRequest());
    }
    pb.clear();
    // 6. check URI privilege
    for (Map.Entry<String, Privilege> uri : analResult.queryStmt_.uriPathRefs) {
      authLoader.checkAccess(user_, pb.onURI(uri.getKey()).allOf(uri.getValue()).toRequest());
    }
  }
}
