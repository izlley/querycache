package com.skplanet.querycache.server.util;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.QCConfigKeys;
import com.skplanet.querycache.server.QueryCacheServer;
import com.skplanet.querycache.server.sqlcompiler.Analyzer;
import com.skplanet.querycache.server.sqlcompiler.QueryStmt.QueryType;
import com.skplanet.querycache.server.StmtNode.State;

public class RuntimeProfile {
  private static final Logger LOG = LoggerFactory.getLogger(RuntimeProfile.class);
  public static final Logger queryAuditLog = LoggerFactory
    .getLogger(RuntimeProfile.class.getName() + ".queryAudit");
  
  private static AtomicInteger numOfRequests = new AtomicInteger(0);
  private static int requestsPer10Sec = 0;
  
  private static int completeQueryMaxCnt = QueryCacheServer.conf.getInt(
    QCConfigKeys.QC_QUERY_PROFILE_COMPLETE_RETENTION_CNT,
    QCConfigKeys.QC_QUERY_PROFILE_COMPLETE_RETENTION_CNT_DEFAULT);
  private static Map<String, QueryProfile> runningQueryProfile = Collections
    .synchronizedMap(new LinkedHashMap<String, QueryProfile>(64));
  private static Map<String, QueryProfile> completeQueryProfile = Collections
    .synchronizedMap(new LinkedHashMap<String, QueryProfile>(completeQueryMaxCnt));
  
  private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  public static class QueryProfile {
    public String queryId = null;
    public String connType = null;
    public String user = null;
    public QueryType queryType = null;
    public String queryStr = null;
    public State stmtState = State.CLOSE;
    public long rowCnt = -1;
    public long startTime = 0;
    public long endTime = 0;
    // 0:exec/1:getmeta/2:fetch/3:stmtclose
    public long[] timeHistogram = {0,0,0,0};
    public long[] execProfile   = null;
    public long[] fetchProfile  = {0,0,0,-1,-1,-1,-1,0,0,-1,-1};
  }
  
  private Runnable resetNumRequestsThread = new Runnable() {
    public void run() {
      boolean interrupted = false;
      try {
        while (true) {
          try {
            Thread.sleep(10000);
            requestsPer10Sec = numOfRequests.get();
            LOG.debug("Requests per 10sec = " + requestsPer10Sec);
            numOfRequests.set(0);
          } catch (InterruptedException e) {
            // Deliberately ignore
            interrupted = true;
          }
        }
      } finally {
        if (interrupted) {
          Thread.currentThread().interrupt();
        }
      }
    }
  };
  
  public RuntimeProfile() {
    new Thread(resetNumRequestsThread).start();
  }
  
  public QueryProfile addRunningQuery(String key, QueryProfile profile) {
    QueryProfile entry = runningQueryProfile.put(key, profile);
    LOG.debug("Add a profile obj in the runningQueryProfileMap: size="
      + runningQueryProfile.size());
    return entry;
  }
  
  public synchronized QueryProfile addCompletedQuery(String key, QueryProfile profile) {
    if (completeQueryProfile.size() - completeQueryMaxCnt >= 0) {
      // remove the oldest entry
      Iterator<Map.Entry<String, QueryProfile>> iter =
        completeQueryProfile.entrySet().iterator();
      if (iter.hasNext()) {
        iter.next();
        iter.remove();
        LOG.debug("Remove a profile obj in the completeQueryProfileMap: size="
          + completeQueryProfile.size());
      }
    }
    QueryProfile entry = completeQueryProfile.put(key, profile);
    LOG.debug("Add a profile obj in the completeQueryProfileMap: size="
      + completeQueryProfile.size());
    return entry;
  }
  
  public void moveRunToCompleteProfileMap(String qid, State state) {
    if (qid == null) return;
    QueryProfile entry = runningQueryProfile.remove(qid);
    if (entry != null) {
      entry.stmtState = state;
      if (state == State.ERROR)
        entry.endTime = System.currentTimeMillis();
      addCompletedQuery(qid, entry);
      queryAuditLog.info("{\"queryid\":\"" + entry.queryId + "\"," +
        "\"connect_type\":\"" + entry.connType + "\"," +
        "\"user\":\"" + entry.user + "\"," +
        "\"query_type\":\"" + ((entry.queryType!=null)?entry.queryType.toString():"") + "\"," +
        "\"query_str\":\"" + entry.queryStr.replace('"', '\'') + "\"," +
        "\"stmt_state\":\"" + entry.stmtState.toString() + "\"," +
        "\"rowcnt\":\"" + entry.rowCnt + "\"," +
        "\"start_time\":\"" + dateformat.format(new Date(entry.startTime)) + "\"," +
        "\"end_time\":\"" + dateformat.format(new Date(entry.endTime)) + "\"," +
        "\"time_histogram\":[\"" + entry.timeHistogram[0] + "\",\"" + entry.timeHistogram[1] +
          "\",\"" + entry.timeHistogram[2] + "\",\"" + entry.timeHistogram[3] + "\"]," +
        "\"total_elapsedtime\":\"" + (entry.timeHistogram[0] + entry.timeHistogram[1] +
          entry.timeHistogram[2] + entry.timeHistogram[3]) + "\"}"
      );
    } else {
      LOG.warn("The query-id(" + qid + ") is not exist in the runningQueryProfile map.");
    }
  }
  
  public void increaseNumReq() {
    numOfRequests.incrementAndGet();
  }
  
  public int getNumReqPer10s() {
    return requestsPer10Sec;
  }
}
