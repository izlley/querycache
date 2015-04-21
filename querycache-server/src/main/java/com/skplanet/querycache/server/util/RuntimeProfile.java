package com.skplanet.querycache.server.util;

import com.google.gson.Gson;
import com.skplanet.pdp.sentinel.shuttle.QueryCacheAuditSentinelShuttle;
import com.skplanet.querycache.cli.thrift.THandleIdentifier;
import com.skplanet.querycache.cli.thrift.THostInfo;
import com.skplanet.querycache.server.ConnNode;
import com.skplanet.querycache.server.QCConfigKeys;
import com.skplanet.querycache.server.QueryCacheServer;
import com.skplanet.querycache.server.StmtNode;
import com.skplanet.querycache.server.StmtNode.State;
import com.skplanet.querycache.server.sqlcompiler.QueryStmt.QueryType;
import com.skplanet.querycache.servlet.QCWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RuntimeProfile {
  private static final boolean DEBUG = false;
  private static final Logger LOG = LoggerFactory.getLogger(RuntimeProfile.class);
  public static final Logger queryAuditLog = LoggerFactory.getLogger("queryAudit");
  
  private static AtomicInteger numOfRequests = new AtomicInteger(0);
  private static AtomicInteger numOfRequestsTotal = new AtomicInteger(0);
  private static int requestsPer10Sec = 0;
  
  private static int completeQueryMaxCnt = QueryCacheServer.conf.getInt(
    QCConfigKeys.QC_QUERY_PROFILE_COMPLETE_RETENTION_CNT,
    QCConfigKeys.QC_QUERY_PROFILE_COMPLETE_RETENTION_CNT_DEFAULT);
  private static Map<String, QueryProfile> runningQueryProfile = Collections
    .synchronizedMap(new LinkedHashMap<String, QueryProfile>(64));
  private static Map<String, QueryProfile> completeQueryProfile = Collections
    .synchronizedMap(new LinkedHashMap<String, QueryProfile>(completeQueryMaxCnt));
  
  private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static SimpleDateFormat dateformatRake = new SimpleDateFormat("yyyyMMddHHmmssSSS");

  public static class QueryProfile {
    public final String queryId;
    public final String connType;
    public final String user;
    public QueryType queryType = null;
    public final String queryStr;
    public final String clientIpReported;
    public State stmtState = State.CLOSE;
    public long rowCnt = -1;
    public final long startTime;
    public long endTime = 0;
    // 0:exec/1:getmeta/2:fetch/3:stmtclose or
    // 1:getschemas/2:fetch
    public Long[] timeHistogram = {0L,0L,0L,0L};
    public long[] execProfile   = null;
    public long[] fetchProfile  = {0,0,0,-1,-1,-1,-1,0,0,-1,-1};
    public final String clientVersion;
    public final String clientIp;
    public final int clientPort;
    public final String serverIp;
    public final int serverPort;
    public final String sessionID;
    public final String backendUrl;

    public QueryProfile(StmtNode sStmt, ConnNode sConn, THandleIdentifier sessionId, String queryStr, QueryCacheServer.QCServerContext svrCtx) {
      this.queryId = sConn.sConnId + ":" + sStmt.sStmtId;
      this.connType = sessionId.driverType;
      this.user = sConn.getUser();
      this.queryStr = (queryStr != null)? queryStr.replace('\n', ' ').replace('\r', ' ').replace('\t', ' '):"";
      THostInfo clientInfo = sConn.getClientInfo();
      this.clientIpReported = clientInfo.getHostname() + "/" + clientInfo.getIpaddr();
      this.startTime = System.currentTimeMillis();
      this.clientVersion = svrCtx.clientVersion;
      this.clientIp = svrCtx.clientIP;
      this.clientPort = svrCtx.clientPort;
      this.serverIp = svrCtx.serverIP;
      this.serverPort = svrCtx.serverPort;
      this.sessionID = svrCtx.sessionID;
      this.backendUrl = sConn.getUrl();
    }

    public void fillShuttleHeader(QueryCacheAuditSentinelShuttle shuttle) {
      shuttle.log_time(dateformatRake.format(new Date(System.currentTimeMillis())));
      shuttle.session_id(this.sessionID);
      shuttle.query_id(this.queryId);
      shuttle.server_hostname(QueryCacheServer.hostname);
      shuttle.server_ip(this.serverIp);
      shuttle.server_port((long)this.serverPort);
      // clientIpReported : "BDBc-t1if010/172.22.213.30""
      shuttle.client_hostname(this.clientIpReported.split("/")[0]);
      shuttle.client_ip(this.clientIp);
      shuttle.client_port((long)this.clientPort);
      shuttle.user_id(this.user);
      try {
        // "jdbc:daas-impala://172.22.224.36:8655"
        shuttle.backend(this.connType.split(":")[1]);
      } catch (Exception e) {
        shuttle.backend(this.connType);
      }
    }
  }
  
  private Runnable resetNumRequestsThread = new Runnable() {
    public void run() {
      boolean interrupted = false;
      try {
        while (true) {
          try {
            Thread.sleep(10000);
            requestsPer10Sec = numOfRequests.getAndSet(0);
            LOG.debug("Requests per 10sec = {}", requestsPer10Sec);
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

  private static RuntimeProfile rpInstance = null;
  public static RuntimeProfile getInstance() {
    if (rpInstance == null) {
      rpInstance = new RuntimeProfile();
    }
    return rpInstance;
  }

  protected RuntimeProfile() {
    new Thread(resetNumRequestsThread).start();
    initEventNotifier();
  }
  
  public QueryProfile addRunningQuery(String key, QueryProfile profile) {
    QueryProfile entry = null;
    synchronized (runningQueryProfile) {
      entry = runningQueryProfile.put(key, profile);
    }

    LOG.debug("Added a profile obj in the runningQueryProfileMap: size={}", runningQueryProfile.size());

    if (entry != null) {
      LOG.warn("There is same query-id in RunningProfileMap (queryId:{})", key);
    }
    addQueryEvent(EVENT_ADD_QUERY, profile);
    return entry;
  }
  
  public QueryProfile addCompletedQuery(String key, QueryProfile profile) {
    QueryProfile entry = null;
    synchronized (completeQueryProfile) {
      if (completeQueryProfile.size() - completeQueryMaxCnt >= 0) {
        // remove the oldest entry
        Iterator<Map.Entry<String, QueryProfile>> iter =
                completeQueryProfile.entrySet().iterator();
        if (iter.hasNext()) {
          iter.next();
          iter.remove();
          LOG.debug("Remove a profile obj in the completeQueryProfileMap: size={}",
                  completeQueryProfile.size());
        }
      }
      entry = completeQueryProfile.put(key, profile);
      LOG.debug("Add a profile obj in the completeQueryProfileMap: size={}",
              completeQueryProfile.size());
    }
    return entry;
  }

  public void moveRunToCompleteProfileMap(String qid, State state) {
    if (qid == null) return;

    QueryProfile entry = null;
    synchronized (runningQueryProfile) {
      entry = runningQueryProfile.remove(qid);
    }

    if (entry != null) {
      entry.stmtState = state;

      addQueryEvent(EVENT_REMOVE_QUERY, entry);

      if (entry.endTime <= 0)
        entry.endTime = System.currentTimeMillis();

      addCompletedQuery(qid, entry);

      QueryCacheAuditSentinelShuttle shuttle = new QueryCacheAuditSentinelShuttle();
      entry.fillShuttleHeader(shuttle);

      shuttle.result((entry.stmtState == State.CLOSE) ? "OK" : "FAIL");
      shuttle.setBodyOfquery_exec(
              entry.queryType.toString(),
              entry.queryStr,
              "querycache", // TODO: use string constant defined elsewhere
              entry.stmtState.toString(),
              entry.rowCnt,
              dateformatRake.format(new Date(entry.startTime)),
              dateformatRake.format(new Date(entry.endTime)),
              Arrays.asList(entry.timeHistogram),
              entry.endTime - entry.startTime,
              entry.backendUrl
              );

      String logStr = shuttle.toString();
      queryAuditLog.info(logStr);
      QueryCacheServer.loggerApi.log("bos-di-admin-querycache-audit", logStr);
    }
  }

  public void increaseNumReq() {
    numOfRequests.incrementAndGet();
    numOfRequestsTotal.incrementAndGet();
  }

  public int getNumReq() {
    return numOfRequestsTotal.get();
  }

  public int getNumReqPer10s() {
    return requestsPer10Sec;
  }

  public List<QueryProfile> getRunningQueries() {
    ArrayList<QueryProfile> list = null;
    synchronized (runningQueryProfile) {
      list = new ArrayList<QueryProfile>(runningQueryProfile.values());
    }
    return list;
  }

  public List<QueryProfile> getCompleteQueries() {
    ArrayList<QueryProfile> list = null;
    synchronized (completeQueryProfile) {
      list = new ArrayList<QueryProfile>(completeQueryProfile.values());
    }
    return list;
  }

  private Map<Integer, QCWebSocket> eventSubscribers = new HashMap<>();
  private List<String> eventQueue = new ArrayList<>();
  private Thread eventNotifier = null;
  private AtomicInteger eventSubscriberId = new AtomicInteger(0);
  public int addSubscriber(QCWebSocket ws) {
    int id = eventSubscriberId.getAndIncrement();
    synchronized (eventSubscribers) {
      eventSubscribers.put(id, ws);
    }
    return id;
  }

  public void removeSubscriber(int id) {
    synchronized (eventSubscribers) {
      eventSubscribers.remove(id);
    }
  }

  private void initEventNotifier() {
    eventNotifier = new Thread( new Runnable() {
      @Override
      public void run() {
        while (!Thread.interrupted()) {
          // wait for event notification
          synchronized (eventQueue) {
            while (eventQueue.size() == 0) {
              try {
                eventQueue.wait();
              } catch (InterruptedException e) {
                break;
              }
            }
          }

          List<String> events = new ArrayList<>();
          synchronized (eventQueue) {
            events.addAll(eventQueue);
            eventQueue.clear();
          }

          List<QCWebSocket> subscribers = new ArrayList<>();
          synchronized (eventSubscribers) {
            subscribers.addAll(eventSubscribers.values());
          }

          // send events to all subscribers.
          for (String event: events) {
            for (QCWebSocket ws: subscribers) {
              try {
                ws.sendMessage(event);
              } catch (Exception e) {
                LOG.error("error sending event", e);
              }
            }
          }
        }
        LOG.info("event notifier thread terminating.");
      }
    });
    eventNotifier.start();
  }

  public static final int EVENT_ADD_QUERY = 0;
  public static final int EVENT_UPDATE_QUERY = 1;
  public static final int EVENT_REMOVE_QUERY = 2;
  private static class RunningQueryEvent {
    public String msgType = null;
    public QueryProfile query = null;
  }

  public void addQueryEvent(int type, QueryProfile query) {
    Gson gson = new Gson();
    RunningQueryEvent e = new RunningQueryEvent();
    switch (type) {
      case EVENT_ADD_QUERY:
        e.msgType = "runningQueryAdded";
        e.query = query;
        break;
      case EVENT_UPDATE_QUERY:
        e.msgType = "runningQueryUpdated";
        e.query = query;
        break;
      case EVENT_REMOVE_QUERY:
        e.msgType = "runningQueryRemoved";
        e.query = query;
        break;
      default:
        LOG.error("BUG: unsupported event type = " + type);
        return;
    }
    String msg = gson.toJson(e);
    synchronized (eventQueue) {
      eventQueue.add(msg);
      eventQueue.notifyAll();
    }
  }
}
