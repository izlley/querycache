package com.skplanet.querycache.server;

import com.skplanet.querycache.cli.thrift.*;
import com.skplanet.querycache.server.common.AnalyzerException;
import com.skplanet.querycache.server.sqlcompiler.Analyzer;
import com.skplanet.querycache.server.sqlcompiler.AuthorizationException;
import com.skplanet.querycache.server.util.RuntimeProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.Future;

/**
 * Created by nazgul33 on 15. 2. 27.
 */
public class StatementExecutor implements Runnable {
    private static final boolean DEBUG = false;
    private static final Logger LOG = LoggerFactory.getLogger(StatementExecutor.class);
    private static final int _defFetchSize = 1024;

    public final boolean asyncMode;
    public final long sConnId;
    public final long sStmtId;
    public final String driverType;
    public final String statement;

    public final ConnNode sConn;
    public final StmtNode sStmt;
    public final TExecuteStatementResp sResp;
    public final TGetOperationStatusResp sOpStatusResp;
    final boolean syntaxCheck, authCheck;
    final long startTime;
    long endTime;
    long timeArr[] = {-1,-1,-1,-1,-1,-1};
    final String sQueryId;
    public final QueryCacheServer.QCServerContext svrCtx;

    public Future job = null;

    public StatementExecutor(TExecuteStatementReq aReq) throws SQLException {
        timeArr[0] = startTime = System.currentTimeMillis();

        asyncMode = aReq.isSetAsyncMode() ? aReq.isAsyncMode() : false;
        sConnId = aReq.sessionHandle.sessionId.connid;
        driverType = aReq.sessionHandle.sessionId.driverType;
        statement = aReq.statement;

        // 1. get connection from pool
        sConn = CLIHandler.gConnMgrs.getConn(driverType, sConnId);
        if (sConn == null) {
            throw new SQLException("Backend connection not available.", "08000");
        }
        timeArr[1] = System.currentTimeMillis();

        // 2. alloc statement
        svrCtx = QueryCacheServer.QCServerContext.getSvrContext();
        sStmt = sConn.allocStmt(true);

        sStmt.profile = new RuntimeProfile.QueryProfile(sStmt, sConn, aReq.sessionHandle.sessionId,
                aReq.statement, svrCtx);
        sStmt.profile.stmtState = StmtNode.State.EXEC;
        sStmt.profile.execProfile = timeArr;
        sStmtId = sStmt.sStmtId;
        sQueryId = sStmt.profile.queryId;
        CLIHandler.gConnMgrs.runtimeProfile.addRunningQuery(sQueryId, sStmt.profile);

        svrCtx.setCurrentQuery(aReq.sessionHandle.sessionId.driverType, sQueryId);

        timeArr[2] = System.currentTimeMillis();

        // 3. Analyze SQL stmt
        // TODO : remove string concatenation by pre-loading authconfig when making connections?
        authCheck = !QueryCacheServer.conf.get(QCConfigKeys.QC_AUTHORIZATION_PREFIX + "." +
                aReq.sessionHandle.sessionId.driverType.split(":")[1], QCConfigKeys.QC_AUTHORIZATION_DEFAULT).
                equalsIgnoreCase("NONE");
        syntaxCheck = QueryCacheServer.conf.getBoolean(QCConfigKeys.QC_QUERY_SYNTAX_CHECK,
                QCConfigKeys.QC_QUERY_SYNTAX_CHECK_DEFAULT);
        try {
            Analyzer compiler = new Analyzer(sConn.user,
                    aReq.sessionHandle.sessionId.driverType, CLIHandler.gConnMgrs);
            compiler.analyzer(aReq.statement, sStmt.profile, authCheck);
        } catch (AuthorizationException e) {
            LOG.error("Authorization error:" + e.getMessage() +
                    "\n  -Error Query: " + aReq.statement, e);
            CLIHandler.gConnMgrs.runtimeProfile.moveRunToCompleteProfileMap(
                    sQueryId, StmtNode.State.ERROR);
            throw new SQLException(e.getMessage(), "28000");
        } catch (AnalyzerException e) {
            LOG.error("Query analyzer error:" + e.getMessage() +
                    "\n  -Error Query: " + aReq.statement, e);
            CLIHandler.gConnMgrs.runtimeProfile.moveRunToCompleteProfileMap(
                    sQueryId, StmtNode.State.ERROR);
            throw new SQLException(e.getMessage(), "26000");
        }
        timeArr[3] = System.currentTimeMillis();

        // build TExecuteStatementResp
        sResp = new TExecuteStatementResp();
        sResp.setStatus(new TStatus().setStatusCode(TStatusCode.SUCCESS_STATUS));

        THandleIdentifier sHI = new THandleIdentifier(sConnId, sStmtId,
                aReq.sessionHandle.sessionId.driverType);
        TOperationHandle sOH = new TOperationHandle();
        sOH.setOperationId(sHI);
        sOH.setOperationType(TOperationType.EXECUTE_STATEMENT);
        sResp.setOperationHandle(sOH);

        // build TGetOperationStatusResp
        sOpStatusResp = new TGetOperationStatusResp();
        sOpStatusResp.setStatus(new TStatus(TStatusCode.SUCCESS_STATUS));
        sOpStatusResp.setOperationState(TOperationState.INITIALIZED_STATE);
    }

    public void run() {
        // 4. execute query
        try {
            sStmt.sHStmt.setFetchSize(_defFetchSize);
        } catch (SQLException e) {
            //ignore
        }

        sOpStatusResp.setOperationState(TOperationState.RUNNING_STATE);
        try {
            boolean sIsRS = sStmt.sHStmt.execute(statement);
            // for better "cancel() handling, mark that "execute" state is finished.
            sStmt.profile.stmtState = StmtNode.State.EXEC_COMPLETE;

            timeArr[4] = System.currentTimeMillis();
            if (sIsRS) {
                sResp.operationHandle.hasResultSet = true;
                // 6. Save a ResultSet in the StmtNode
                sStmt.sHasResultSet = true;
                sStmt.sRS = sStmt.sHStmt.getResultSet();
            } else {
                sResp.operationHandle.hasResultSet = false;
                sResp.operationHandle.updateRowCount = sStmt.sHStmt.getUpdateCount();
                // set profiling data
                sStmt.profile.rowCnt = (long)sResp.operationHandle.updateRowCount;
            }

            sOpStatusResp.setOperationState(TOperationState.FINISHED_STATE);
            sOpStatusResp.setOperationHandle(sResp.operationHandle);

            endTime = System.currentTimeMillis();
            LOG.debug("ExecuteStatement PROFILE: QueryID={}, Type={}, Execute time elapsed={}ms, Query={}",
                    sQueryId, driverType, endTime - startTime, sStmt.profile.queryStr);
            sStmt.profile.timeHistogram[0] = endTime - startTime;

        } catch (SQLException e) {
            LOG.error("ExecuteStatement error(" + e.getSQLState() + ") :" + e.getMessage() +
                    "\n  -Error Query: " + statement, e);
            sOpStatusResp.status.setStatusCode(TStatusCode.ERROR_STATUS);
            sOpStatusResp.status.setSqlState(e.getSQLState());
            sOpStatusResp.status.setErrorCode(e.getErrorCode());
            sOpStatusResp.status.setErrorMessage(e.getMessage());
            // remove failed ConnNode in the UsingMap
            if ("08S01".equals(e.getSQLState())) {
                // remove failed ConnNode in the ConnPool
                CLIHandler.gConnMgrs.removeConn(driverType, sConnId);
                LOG.warn("ExecuteStatement: Removing a failed connection (connId:" + sConn.sConnId + ")");
            }

            switch (e.getSQLState()) {
                case "HY008":
                    sOpStatusResp.setOperationState(TOperationState.CANCELED_STATE); break;
                default:
                    sOpStatusResp.setOperationState(TOperationState.ERROR_STATE); break;
            }
            CLIHandler.gConnMgrs.runtimeProfile.moveRunToCompleteProfileMap(
                    sQueryId, StmtNode.State.ERROR);
        }
    }

}
