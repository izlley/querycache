package com.skplanet.querycache.server.sqlcompiler;

import com.google.common.base.Preconditions;
import com.skplanet.pdp.sentinel.shuttle.QueryCacheAuditSentinelShuttle;
import com.skplanet.querycache.server.ConnMgrCollection;
import com.skplanet.querycache.server.QueryCacheServer;
import com.skplanet.querycache.server.auth.AuthorizationLoader;
import com.skplanet.querycache.server.auth.Privilege;
import com.skplanet.querycache.server.auth.PrivilegeRequestBuilder;
import com.skplanet.querycache.server.common.AnalyzerException;
import com.skplanet.querycache.server.util.RuntimeProfile.QueryProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Map;

/**
 * Wrapper class for parser and analyzer.
 */
public class Analyzer {
  private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);
  public static final Logger authzAuditLog = LoggerFactory.getLogger("authzAudit");
  private String stmt_;
  private final String user_;
  private final String connType_;
  private final ConnMgrCollection connMgrs_;

  private final String accessGranted = "GRANTED";
  private final String accessDenied = "DENIED";
  
  private AnalysisResult analResult;
  
  public Analyzer(String user, String connType, ConnMgrCollection connMgrs) {
    this.user_ = (user == null)? "" : user;
    this.connType_ = connType;
    this.connMgrs_ = connMgrs;
  }
  
  static public class AnalysisResult {
    private QueryStmt queryStmt_;
    
    public QueryStmt getQueryStmt() {
      return queryStmt_;
    }
  }
  
  public void analyzer(String stmt, QueryProfile profile, boolean isAuthCheck) throws AuthorizationException, AnalyzerException {
    this.stmt_ = stmt;
    analResult = new AnalysisResult();
    
    SqlScanner input = new SqlScanner(new StringReader(stmt));
    SqlParser parser = new SqlParser(input);
    parser.datastore = connType_.split(":")[1];
    try {
      analResult.queryStmt_ = (QueryStmt) parser.parse().value;
      if (analResult.queryStmt_ == null) return;

      LOG.debug("User={}, QueryStmt: QueryType={}\n"
              + "[limit,filter,nested,join,with,invalidateall]=[{},{},{},{},{},{}]\n"
              + "[nestedLvl,unionLvl]=[{},{}]\n"
              + "Datastore={}, Database=[{}], Table=[{}], View=[{}], With={}\n"
              + "Function=[{}], Uris={}",
              user_, analResult.queryStmt_.type.toString(),
              analResult.queryStmt_.isLimit, analResult.queryStmt_.isFilterCond,
              analResult.queryStmt_.isNested, analResult.queryStmt_.isJoin,
              analResult.queryStmt_.isWith, analResult.queryStmt_.isInvalidateAll,
              analResult.queryStmt_.nestedLvl, analResult.queryStmt_.unionLvl,
              analResult.queryStmt_.dataStoreRefs.toString(),
              analResult.queryStmt_.toStringdatabaseRefs() +
              analResult.queryStmt_.toStringtableRefs() +
              analResult.queryStmt_.toStringviewRefs() +
              analResult.queryStmt_.withRefs.toString() +
              analResult.queryStmt_.toStringfunctionRefs() +
              analResult.queryStmt_.uriPathRefs.toString());

      // set profiling data
      if (profile!=null) {
        profile.queryType = analResult.queryStmt_.type;
      }
      if (isAuthCheck) {
        checkAuthorization(analResult, profile);
      }
    } catch (AuthorizationException e) {
      throw e;
    } catch (Exception e) { // includes syntax error
      String msg = parser.getErrorMsg(stmt);
      throw new AnalyzerException((msg!=null)? msg: e.getMessage(), e);
    }
  }

  void checkAuthorization(Analyzer.AnalysisResult analResult, QueryProfile profile)
      throws AuthorizationException {
    Preconditions.checkNotNull(analResult.queryStmt_);
    String authzlog = "";
    String auditquery = stmt_.replace('\t',' ').replace('\n',' ');
    PrivilegeRequestBuilder pb = new PrivilegeRequestBuilder();
    AuthorizationLoader authLoader = connMgrs_.getAuthLoader(connType_);

    QueryCacheAuditSentinelShuttle shuttle = new QueryCacheAuditSentinelShuttle();
    profile.fillShuttleHeader(shuttle);

    // 1. check Datastore privilege
    for (Map.Entry<String, Privilege> ds : analResult.queryStmt_.dataStoreRefs) {
      String access_status;
      AuthorizationException e = null;
      try {
        authLoader.checkAccess(user_, pb.onServer(ds.getKey()).allOf(ds.getValue()).toRequest());
        access_status = accessGranted;
      } catch (AuthorizationException e_) {
        access_status = accessDenied;
        e = e_;
      }
      shuttle.result((access_status.equals(accessGranted)) ? "OK" : "FAIL");
      shuttle.setBodyOfdata_access_check(
              access_status, analResult.queryStmt_.type.toString(), ds.getValue().toString(),
              ds.getKey(), null, null, null, null, null );
      String authLog = shuttle.toString();
      authzAuditLog.info(authLog);
      QueryCacheServer.loggerApi.log("bos-di-admin-querycache-audit", authLog);
      if (e!=null) throw e;
    }
    pb.clear();
    // 2. check Database privilege
    for (Map.Entry<DatabaseName, Privilege> db : analResult.queryStmt_.databaseRefs) {
      String access_status;
      AuthorizationException e = null;
      try {
        authLoader.checkAccess(user_, pb.onDb(db.getKey().getDb()).allOf(db.getValue()).toRequest());
        access_status = accessGranted;
      } catch (AuthorizationException e_) {
        access_status = accessDenied;
        e = e_;
      }
      shuttle.result((access_status.equals(accessGranted)) ? "OK" : "FAIL");
      shuttle.setBodyOfdata_access_check(
              access_status, analResult.queryStmt_.type.toString(), db.getValue().toString(),
              db.getKey().getDs(), db.getKey().getDb(), null, null, null, null );
      String authLog = shuttle.toString();
      authzAuditLog.info(authLog);
      QueryCacheServer.loggerApi.log("bos-di-admin-querycache-audit", authLog);
      if (e!=null) throw e;
    }
    pb.clear();
    // 3. check Table privilege
    for (Map.Entry<TableName, Privilege> table : analResult.queryStmt_.tableRefs) {
      if (analResult.queryStmt_.withRefs.contains(table.getKey().getTable())
          && table.getKey().getDb().equals("default"))
        continue;
      String access_status;
      AuthorizationException e = null;
      try {
        authLoader.checkAccess(user_,
          pb.onTable(table.getKey().getDb(), table.getKey().getTable())
            .allOf(table.getValue()).toRequest());
        access_status = accessGranted;
      } catch (AuthorizationException e_) {
        access_status = accessDenied;
        e = e_;
      }
      shuttle.result((access_status.equals(accessGranted)) ? "OK" : "FAIL");
      shuttle.setBodyOfdata_access_check(
              access_status, analResult.queryStmt_.type.toString(), table.getValue().toString(),
              null, table.getKey().getDb(), table.getKey().getTable(), null, null, null );
      String authLog = shuttle.toString();
      authzAuditLog.info(authLog);
      QueryCacheServer.loggerApi.log("bos-di-admin-querycache-audit", authLog);
      if (e!=null) throw e;
    }
    pb.clear();
    // 4. check View privilege
    for (Map.Entry<TableName, Privilege> vw : analResult.queryStmt_.viewRefs) {
      String access_status;
      AuthorizationException e = null;
      try {
        authLoader.checkAccess(user_,
          pb.onTable(vw.getKey().getDb(), vw.getKey().getTable())
            .allOf(vw.getValue()).toRequest());
        access_status = accessGranted;
      } catch (AuthorizationException e_) {
        access_status = accessDenied;
        e = e_;
      }
      shuttle.result((access_status.equals(accessGranted)) ? "OK" : "FAIL");
      shuttle.setBodyOfdata_access_check(
              access_status, analResult.queryStmt_.type.toString(), vw.getValue().toString(),
              null, vw.getKey().getDb(), null, null, vw.getKey().getTable(), null );
      String authLog = shuttle.toString();
      authzAuditLog.info(authLog);
      QueryCacheServer.loggerApi.log("bos-di-admin-querycache-audit", authLog);
      if (e!=null) throw e;
    }
    pb.clear();
    // 5. check Function privilege
    for (Map.Entry<FuncName, Privilege> func : analResult.queryStmt_.functionRefs) {
      if (!authLoader.isUDFExist(func.getKey().getFunc()))
        continue;
      String access_status;
      AuthorizationException e = null;
      try {
        authLoader.checkAccess(user_,
          pb.onFunction(func.getKey().getDb(), func.getKey().getFunc())
            .allOf(func.getValue()).toRequest());
        access_status = accessGranted;
      } catch (AuthorizationException e_) {
        access_status = accessDenied;
        e = e_;
      }
      shuttle.result((access_status.equals(accessGranted)) ? "OK" : "FAIL");
      shuttle.setBodyOfdata_access_check(
              access_status, analResult.queryStmt_.type.toString(), func.getValue().toString(),
              null, func.getKey().getDb(), null, null, null, func.getKey().getFunc() );
      String authLog = shuttle.toString();
      authzAuditLog.info(authLog);
      QueryCacheServer.loggerApi.log("bos-di-admin-querycache-audit", authLog);
      if (e!=null) throw e;
    }
    pb.clear();
    // 6. check URI privilege
    for (Map.Entry<String, Privilege> uri : analResult.queryStmt_.uriPathRefs) {
      String access_status;
      AuthorizationException e = null;
      try {
        authLoader.checkAccess(user_,
          pb.onURI(uri.getKey()).allOf(uri.getValue()).toRequest());
        access_status = accessGranted;
      } catch (AuthorizationException e_) {
        access_status = accessDenied;
        e = e_;
      }
      shuttle.result((access_status.equals(accessGranted)) ? "OK" : "FAIL");
      shuttle.setBodyOfdata_access_check(
              access_status, analResult.queryStmt_.type.toString(), uri.getValue().toString(),
              null, null, null, uri.getKey(), null, null );
      String authLog = shuttle.toString();
      authzAuditLog.info(authLog);
      QueryCacheServer.loggerApi.log("bos-di-admin-querycache-audit", authLog);
      if (e!=null) throw e;
    }
  }
  
  public AnalysisResult getAnalysisResult() {
    return analResult;
  }
}
