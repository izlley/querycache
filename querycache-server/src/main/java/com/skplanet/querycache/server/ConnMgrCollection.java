package com.skplanet.querycache.server;

import com.google.common.base.Joiner;
import com.skplanet.querycache.server.auth.AuthorizationConfig;
import com.skplanet.querycache.server.auth.AuthorizationLoader;
import com.skplanet.querycache.server.common.InternalType.CORE_RESULT;
import com.skplanet.querycache.server.util.RuntimeProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ConnMgrCollection {
  private static final boolean DEBUG = false;
  private static final Logger LOG = LoggerFactory.getLogger(ConnMgrCollection.class);

  // TODO: generate query operation handle represented as a string
  //       something like "38AE2A-3ACD9374A3E1"

  // generate unique "backend connection id"
  private static final AtomicLong sConnIdGen = new AtomicLong(0L);
  private static long getNewConnId() {
    long id;
    // TODO: find a way to remove "synchronized" and generate "positive numbers only"
    synchronized (sConnIdGen) {
      id = sConnIdGen.incrementAndGet();
      if (id < 0L)  {
        sConnIdGen.set(1L);
        id = 1L;
      }
    }
    return id;
  }

  public class ConnMgr {
    public final String connType;
    public ConnMgr(String connType) {
      this.connType = connType;
    }

    // N.B.: if the list is structurally modified at any time after the iterator
    // is
    // created, in any way except through the Iterator's own remove or add
    // methods,
    // the iterator will throw a ConcurrentModificationException
    // TODO: 1. more sophisticated concurrency control is needed
    // 2. We need to reduce FreeList periodically to prevent from expanding
    // infinitely
    private List<ConnNode> sFreeList = Collections
        .synchronizedList(new LinkedList<ConnNode>());
    // TODO: We need a periodic garbage collection of zombie connections to
    // prevent unintended
    // UsingMap expansion.
    private ConcurrentHashMap<Long, ConnNode> sUsingMap = new ConcurrentHashMap<>(
        16, 0.9f, 1);

    // below properties are used for connecting to storage system
    private ConnProperty connProp = new ConnProperty();
    // index of connection addresses for failover control in a round-robin fashion
    private Short connAddrIndex = -1;

    // managing authorization control
    private AuthorizationLoader authLoader = null;

    // is pheonix?
    private boolean isPhoenix = false;

    // It's a checker thread running in the background to extend the size of the ConnPool.
    private ConnNode newConnNode() throws LinkageError, ClassNotFoundException {
      ConnNode sConn;
      String url = buildUrl();
      try {
        sConn = new ConnNode(connProp, getNewConnId(), url);
      } catch (SQLException e) {
        LOG.error( "newConnNode() : Connection error to (" + url + ") " + ":" + e.getMessage(), e);
        return null;
      }
      LOG.info("newConnNode() : {}", url);
      return sConn;
    }

    private void resizeConnectionPool() {
      LOG.debug("ConnPool resizing.");
      int currFreeSize = sFreeList.size();
      int currUsingSize = sUsingMap.size();

      if (currFreeSize > sFreeListInitSize) {
        int removeCount = 0;
        // slowly remove connections : max 1/4 of init size
        int removeConnNo = currFreeSize - sFreeListInitSize;
        int removeConnMax = ((sFreeListInitSize / 4) == 0)? 1:(sFreeListInitSize / 4);
        if (removeConnNo > removeConnMax) {
          removeConnNo = removeConnMax;
        }

        for (int i = 0; i < removeConnNo; i++) {
          ConnNode sConn;
          try {
            sConn = sFreeList.remove(0);
          } catch (IndexOutOfBoundsException e) {
            // no more conns instance in free list. stop removal
            break;
          }

          try {
            sConn.close();
          } catch (Exception e) {
            LOG.error("Resizing worker: [" + connProp.connTypeName + "] Error removing excessive free connections :"
                    + e.getMessage(), e);
          }
          removeCount++;
        }
        LOG.info("Resizing worker: [" + connProp.connTypeName + "] Removed " + removeCount);
      }
      else if (currFreeSize <= (int) (sFreeListInitSize * sFreelistThreshold + 1)) {
        try {
          int addedCnt = 0;
          LOG.info("Resizing worker: Resizing more ConnNode in the ConnPool, Type="
                  + connProp.connTypeName + ", current freesize="
                  + currFreeSize + ", current usingsize=" + currUsingSize
                  + ", max size=" + sFreeListInitSize);
          // resizing connpool
          for (int i = (int) sFreeListInitSize; i < ((sFreeListInitSize * 3) / 2 + 1); i++) {
            ConnNode sConn = newConnNode();
            if (sConn == null) {
              LOG.error("Error resizing connection pool for " + connProp.connTypeName);
            } else {
              sFreeList.add(sConn);
              addedCnt++;
            }
          }
          LOG.info("Resizing worker: FreeList resizing from " + currFreeSize + " to "
                  + (currFreeSize + addedCnt));
        } catch (Exception e) {
          LOG.error("Resizing worker: [" + connProp.connTypeName + "] Error making more connections :"
                  + e.getMessage(), e);
        }
      }
    }

    // check some connections by executing a query
    private void checkConnections(){
      LOG.debug("ConnPool checking.");
      try {
        // we might see same connection object in one iteration.
        ArrayList<ConnNode> checkedList = new ArrayList<>(sFreeList.size());
        int maxIter = sFreeList.size();
        for (int i = 0; i < maxIter; i++) {
          ConnNode conn;
          try {
            conn = sFreeList.remove(0);
          } catch (IndexOutOfBoundsException e) {
            // ignore : just go out of for loop
            break;
          }

          // check if we had checked this connNode already
          if (checkedList.contains(conn)) {
            LOG.info("ConnPool GC: finishing iterarion due to collision");
            break;
          }

          checkedList.add(conn);

          Statement stmt = null;
          boolean killConnection = false;
          try {
            stmt = conn.sHConn.createStatement();
            String query = QueryCacheServer.conf.get(
                    QCConfigKeys.QC_CONNECTIONPOOL_GC_VERIFY_QUERY,
                    QCConfigKeys.QC_CONNECTIONPOOL_GC_VERIFY_QUERY_DEFAULT);
            // execute call need communication with storage server.
            stmt.execute(query);
            LOG.debug("ConnPool GC: execute query, url=" + conn.getUrl() + ",query=" + query);
            stmt.close();
          } catch (SQLException e) {
            // only handling the connection error
            if ("08S01".equals(e.getSQLState())) {
              LOG.info("ConnPool GC: Removing a failed connection (connId:" + conn.sConnId + ")");
              // do not add back to free list.
              killConnection = true;
            }
          } finally {
            if (stmt != null) {
              try {
                stmt.close();
              } catch (SQLException e) {
                // just ignore
                LOG.warn("ConnPool GC: closing stmt error (connId:" + conn.sConnId + ")" +
                        ", " + e.getMessage());
              }
              if (killConnection) {
                conn.close();
              } else {
                sFreeList.add(conn);
              }
            }
          }
        }
        checkedList.clear();
      } catch (Exception e) {
        LOG.error("ConnPool GC: [" + connProp.connTypeName + "] GC thread error :" + e.getMessage(), e);
      }
    }
    
    private CORE_RESULT initialize(String aConnType,
                                  ConnProperty.protocolType aProtoType) {
      if (connProp.setConnProperties(aConnType, aProtoType) != CORE_RESULT.CORE_SUCCESS)
        return CORE_RESULT.CORE_FAILURE;

      isPhoenix = connProp.connPkgPath.equalsIgnoreCase("org.apache.phoenix.jdbc.PhoenixDriver");

      LOG.info("Initializing the connection pool of " + aConnType + "...");
      // TODO: How can we handle url kv options? just ignore these?
      int numofRealConn = 0;
      for (int i = 0; i < sFreeListInitSize; i++) {
        try {
          ConnNode sConn = newConnNode();
          if (sConn == null) {
            continue;
          } else {
            ++numofRealConn;
            sFreeList.add(sConn);
          }
        } catch (LinkageError e) {
          LOG.error( "connection init error : " + connProp.connTypeName + " : " + e.getMessage(), e);
          return CORE_RESULT.CORE_FAILURE;
        } catch (ClassNotFoundException e) {
          LOG.error( "connection init error : " + connProp.connTypeName + " : " + e.getMessage(), e);
          return CORE_RESULT.CORE_FAILURE;
        }

        LOG.debug("{} conn pool initialized ({} connNodes)", aConnType, numofRealConn);
      }

      return CORE_RESULT.CORE_SUCCESS;
    }

    private ConnNode getConn(long aConnId) {
      return sUsingMap.get(aConnId);
    }

    private void removeConn(long aConnId) {
      ConnNode sConn = sUsingMap.remove(aConnId);
      if (sConn == null) {
        LOG.warn(
          "ConnMgrofOne.removeConn(): non-existent connId (" + aConnId + ")");
      } else {
        LOG.info("Removing a failed connection (connId:" + sConn.sConnId + ")");
      }
    }
    
    private ConnNode allocConn() {
      // 1. get a ConNode from the FreeList
      // O(1)
      ConnNode sConn;
      try {
        sConn = sFreeList.remove(0);
      } catch (IndexOutOfBoundsException e) {
        // if there is no remaining connection, set null
        sConn = null;
      }
      
      // 2. if no ConNode in the ConnPool, make connection explicitly
      if (sConn == null) {
        // retry count is not configurable
        short retryCnt = 3;
        for (int i = 0; i < retryCnt; i++) {
          try {
            ConnNode sConn2 = newConnNode();
            if (sConn2 == null) {
              LOG.info("Retrying connection");
              continue;
            }
            sConn = sConn2;
          } catch (Exception e) {
            LOG.error("allocConn() error : " + e.getMessage(), e);
          }
        }
      }
      
      if (sConn != null) {
        // O(1)
        sUsingMap.put(sConn.sConnId, sConn);
        LOG.trace("Moving ConnNode from FreeList to UsingMap. -Type: {}, -Free size: {}, -Using size:{}",
                connProp.connTypeName, sFreeList.size(), sUsingMap.size());
      }

      return sConn;
    }

    private CORE_RESULT closeConn(long aConnId) {
      ConnNode sConn = sUsingMap.remove(aConnId);
      if (sConn == null) {
        LOG.debug("ConnMgrofOne.removeConn(): already closed. (" + aConnId + ")");
        return CORE_RESULT.CORE_SUCCESS;
      }
      // close all statements in the ConnNode
      try {
        sConn.closeAllStmts();
        sConn.user = null;
        sConn.setPassword(null);
      } catch (SQLException e1) {
        LOG.error("Statement close error :" + e1.getMessage(), e1);
        // force close connection
        sConn.close();
        return CORE_RESULT.CORE_FAILURE;
      }

      // add ConnNode to FreeList
      sFreeList.add(sConn);
      LOG.trace("Moving ConnNode from UsingMap to FreeList. -Free size:{}, -Using size:{}",
              sFreeList.size(), sUsingMap.size());
      return CORE_RESULT.CORE_SUCCESS;
    }
    
    private String buildUrl() {
      // Every time builUrl is called, returns different address for distributing connections
      StringBuffer sb = new StringBuffer();
      if (isPhoenix) {
        sb.append(connProp.connUrlPrefix)
                .append(Joiner.on(",").join(connProp.connAddr))
                .append(':')
                .append(connProp.connPort);
        /*
        sUrl = connProp.connUrlPrefix;
        for (short i = 0; i < connProp.connAddr.length; i++) {
          sUrl += connProp.connAddr[i];
          if (i != connProp.connAddr.length - 1)
            sUrl += ',';
        }
        sUrl += ":" + connProp.connPort;
        */
      } else {
        short sInd;
        synchronized (connAddrIndex) {
          sInd = connAddrIndex = (short)((++connAddrIndex) % connProp.connAddr.length);
          /*
          sInd = (connProp.connAddr.length - 1 <= connAddrIndex) ? connAddrIndex = 0
              : ++connAddrIndex;
              */
        }
        sb.append(connProp.connUrlPrefix)
          .append(connProp.connAddr[sInd])
          .append(':')
          .append(connProp.connPort);

        /*
        sUrl = connProp.connUrlPrefix + connProp.connAddr[sInd] + ":"
            + connProp.connPort;
            */
      }
      if (!connProp.connUrlSuffix.isEmpty()) {
        sb.append('/')
          .append(connProp.connUrlSuffix);
        // sUrl += "/" + connProp.connUrlSuffix;
      }

      return sb.toString();
    }

    public int getFreeConnCount() {
      return sFreeList.size();
    }
    public int getUsingConnCount() {
      return sUsingMap.size();
    }
  }

  private ScheduledExecutorService connPoolMgmtService;
  private int sFreeListInitSize;
  private float sFreelistThreshold; // def:0.2f

  // This Array contains ConnMgrofOnes of all of connection types.
  private Map<String, ConnMgr> connMgrMap = new HashMap<>();
  // This contains running and completed query's profiling data
  public RuntimeProfile runtimeProfile = RuntimeProfile.getInstance();

  CORE_RESULT initialize() {
    final ArrayList<ConnMgr> gcEnabledMgrs = new ArrayList<>();
    final long sGCCycle = QueryCacheServer.conf.getLong(
            QCConfigKeys.QC_CONNECTIONPOOL_GC_CYCLE_MILLI,
            QCConfigKeys.QC_CONNECTIONPOOL_GC_CYCLE_MILLI_DEFAULT); // def:5*60*1000;
    final long sResizingCycle = QueryCacheServer.conf.getLong(
            QCConfigKeys.QC_CONNECTIONPOOL_RESIZING_CYCLE_MILLI,
            QCConfigKeys.QC_CONNECTIONPOOL_RESIZING_CYCLE_MILLI_DEFAULT); // def:15*1000

    // get properties for initialize ConnMgr
    sFreeListInitSize = QueryCacheServer.conf.getInt(
            QCConfigKeys.QC_CONNECTIONPOOL_FREE_INIT_SIZE,
            QCConfigKeys.QC_CONNECTIONPOOL_FREE_INIT_SIZE_DEFAULT);
    sFreelistThreshold = QueryCacheServer.conf.getFloat(
            QCConfigKeys.QC_CONNECTIONPOOL_FREE_RESIZING_F,
            QCConfigKeys.QC_CONNECTIONPOOL_FREE_RESIZING_F_DEFAULT);

    String[] sDrivers = QueryCacheServer.conf.getStrings(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS);
    // initialize jdbc connpools
    for (String driver: sDrivers) {
      ConnMgr sConn = new ConnMgr(driver);
      if (sConn.initialize(driver, ConnProperty.protocolType.JDBC)
            == CORE_RESULT.CORE_FAILURE)
        continue;

      if (QueryCacheServer.conf.getBoolean(QCConfigKeys.QC_CONNECTIONPOOL_GC_PREFIX +
              "." + driver, QCConfigKeys.QC_CONNECTIONPOOL_GC_DEFAULT)) {
        gcEnabledMgrs.add(sConn);
      }

      // Load Sentry configuration for authorization control
      if (QueryCacheServer.conf.get(
           QCConfigKeys.QC_AUTHORIZATION_PREFIX + "." + driver, QCConfigKeys.QC_AUTHORIZATION_DEFAULT).
             equalsIgnoreCase("SENTRY")) {
        AuthorizationConfig authorizationConfig = new AuthorizationConfig(
          driver,
          QueryCacheServer.conf.get(
            QCConfigKeys.QC_STORAGE_AUTH_POLICY_FILE_PREFIX + "." + driver),
          QueryCacheServer.conf.get(
            QCConfigKeys.QC_STORAGE_AUTH_UDF_WHITELIST_FILE_PREFIX + "." + driver, null),
          QueryCacheServer.conf.get(
            QCConfigKeys.QC_STORAGE_AUTH_POLICY_PROVIDER_CLASS_PREFIX + "." + driver,
            "org.apache.sentry.provider.file.LocalGroupResourceAuthorizationProvider")
        );
        authorizationConfig.validateConfig();
        sConn.authLoader = new AuthorizationLoader(authorizationConfig);
      }
      
      connMgrMap.put("jdbc:" + driver, sConn);
    }
    
    // initialize hbase connpool
    String sHBaseDriver = QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_HBASE_DRIVER);
    
    if (sHBaseDriver != null) {
      ConnMgr sConn = new ConnMgr(sHBaseDriver);
      if (sConn.initialize(sHBaseDriver, ConnProperty.protocolType.HBASE)
            == CORE_RESULT.CORE_FAILURE) {
        LOG.error("Connection error to " + sHBaseDriver);
      } else {
        connMgrMap.put("hbase:" + sHBaseDriver, sConn);
      }
    }

    // schedule management tasks
    Runnable gcTask = new Runnable() {
      public void run() {
        for (ConnMgr mgr : gcEnabledMgrs) {
          mgr.checkConnections();
        }
      }
    };
    Runnable resizeTask = new Runnable() {
      public void run() {
        for (ConnMgr mgr : connMgrMap.values()) {
          mgr.resizeConnectionPool();
        }
      }
    };
    connPoolMgmtService = Executors.newScheduledThreadPool(1);
    connPoolMgmtService.scheduleAtFixedRate(gcTask, sGCCycle, sGCCycle, TimeUnit.MILLISECONDS);
    connPoolMgmtService.scheduleAtFixedRate(resizeTask, sResizingCycle, sResizingCycle, TimeUnit.MILLISECONDS);

    return CORE_RESULT.CORE_SUCCESS;
  }

  public ConnNode getConn(String aType, long aConnId) {
    ConnMgr sConnMgr = connMgrMap.get(aType);
    if (sConnMgr == null) {
      return null;
    }
    return sConnMgr.getConn(aConnId);
  }

  public void removeConn(String aType, long aConnId) {
    ConnMgr sConnMgr = connMgrMap.get(aType);
    if (sConnMgr == null)
      return;
    sConnMgr.removeConn(aConnId);
  }
  
  public ConnNode allocConn(String aType) {
    ConnMgr sConnMgr = connMgrMap.get(aType);
    if (sConnMgr == null) {
      return null;
    }
    return sConnMgr.allocConn();
  }

  public CORE_RESULT closeConn(String aType, long aConnId) {
    ConnMgr sConnMgr = connMgrMap.get(aType);
    if (sConnMgr == null) {
      return null;
    }
    return sConnMgr.closeConn(aConnId);
  }
  
  public AuthorizationLoader getAuthLoader(String aType) {
    ConnMgr sConnMgr = connMgrMap.get(aType);
    if (sConnMgr == null) {
      return null;
    }
    return sConnMgr.authLoader;
  }
  
  public Map<String, ConnMgr> getConnMgrMap() {
    return this.connMgrMap;
  }
}
