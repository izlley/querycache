package com.skplanet.querycache.server;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.auth.AuthorizationConfig;
import com.skplanet.querycache.server.auth.AuthorizationLoader;
import com.skplanet.querycache.server.common.InternalType.CORE_RESULT;
import com.skplanet.querycache.server.util.RuntimeProfile;

public class ConnMgr {
  private static final boolean DEBUG = false;
  private static final Logger LOG = LoggerFactory.getLogger(ConnMgr.class);

  // TODO: use hashcode instead of long
  // Long.MAX_VALUE = 9223372036854775807

  // moved to here and switched to static, in order to make connection id unique.
  // some code in qc server use [conid]:[qid] as query id
  private static Long sConnIdGen = new Long(0L);
  private static long getNewConnId() {
    long id;
    synchronized (sConnIdGen) {
      sConnIdGen++;
      if (sConnIdGen < 0L) sConnIdGen = 1L;
      id = sConnIdGen;
    }
    return id;
  }

  public class ConnMgrofOne {
    public final String connType;
    public ConnMgrofOne(String _connType) {
      connType = new String(_connType);
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
    private ConcurrentHashMap<Long, ConnNode> sUsingMap = new ConcurrentHashMap<Long, ConnNode>(
        16, 0.9f, 1);

    private float sFreelistThreshold; // def:0.2f
    private AtomicLong sFreelistMaxSize = new AtomicLong(0L); // def:16
    private long sResizingCycle; // def:15*1000
    private long sGCCycle; // def:5*60*1000;

    // below properties are used for connecting to storage system
    private ConnProperty connProp = new ConnProperty();
    // index of connection addresses for failover control in a round-robin fashion
    private short connAddrIndex = -1;
    
    // managing authorization control
    private AuthorizationLoader authLoader = null;
    
    // It's a checker thread running in the background to extend the size of the ConnPool.
    private ConnNode newConnNode() throws LinkageError, ClassNotFoundException {
      ConnNode sConn = null;
      boolean isPhoenix = connProp.connPkgPath.equalsIgnoreCase("org.apache.phoenix.jdbc.PhoenixDriver");
      // Every time builUrl is called, returns different address for distributing connections
      String url = buildUrl(isPhoenix);
      try {
        sConn = new ConnNode(connProp, getNewConnId(), url);
      } catch (SQLException e) {
        LOG.error( "newConnNode() : Connection error to (" + url + ") " + ":" + e.getMessage(), e);
        return null;
      }
      LOG.info("newConnNode() : {}", url);
      sFreeList.add(sConn);
      sFreelistMaxSize.addAndGet(1);
      return sConn;
    }

    private Runnable sReloadThread = new Runnable() {
      public void run() {
        LOG.info("ConnPool resizing thread init.");
        boolean interrupted = false;
        try {
          while (true) {
            try {
              Thread.sleep(sResizingCycle);
              int currFreeSize = sFreeList.size();
              int currUsingSize = sUsingMap.size();
              long maxSize = sFreelistMaxSize.get();
              int  addedCnt = 0;
              String url = "";
              if (currFreeSize <= (int)(maxSize * sFreelistThreshold + 1)) {
                LOG.info("Resizing worker: Resizing more ConnNode in the ConnPool, Type="
                  + connProp.connTypeName + ", current freesize="
                  + currFreeSize + ", current usingsize=" + currUsingSize
                  + ", max size=" + maxSize);
                // resizing connpool
                for (int i = (int)maxSize; i < ((maxSize * 3) / 2 + 1); i++) {
                  ConnNode sConn = newConnNode();
                  if (sConn == null) {
                    LOG.error("Error resizing connection pool for " + connProp.connTypeName);
                  } else {
                    addedCnt++;
                  }
                }
                LOG.info("Resizing worker: FreeList resizing from " + currFreeSize + " to "
                    + (currFreeSize + addedCnt));
              }
            } catch (InterruptedException e) {
              // Deliberately ignore
              interrupted = true;
            } catch (Exception e) {
              // just ignore
              LOG.error("Resizing worker: [" + connProp.connTypeName + "] Resising thread error :"
                + e.getMessage(), e);
            }
          }
        } finally {
          if (interrupted) {
            Thread.currentThread().interrupt();
          }
        }
      }
    };
    
    // It's a checker thread running in the background to detect failed connections 
    // and remove those connections.
    private Runnable sGCThread = new Runnable() {
      public void run() {
        LOG.info("ConnPool GC thread init.");
        boolean interrupted = false;
        try {
          while (true) {
            try {
              Thread.sleep(sGCCycle);
              LOG.info("ConnPool GC: [" + connProp.connTypeName + "] ConnPool GC activated...");
              int currSize = sFreeList.size();
              for (int i = currSize - 1; i >= 0; --i) {
                // it's not good for performance, but we can avoid ConcurrentModificationException(CME)
                ConnNode conn = sFreeList.get(i);
                Statement stmt = null;
                try {
                  stmt = conn.sHConn.createStatement();
                  String query = QueryCacheServer.conf.get(
                    QCConfigKeys.QC_CONNECTIONPOOL_GC_VERIFY_QUERY,
                    QCConfigKeys.QC_CONNECTIONPOOL_GC_VERIFY_QUERY_DEFAULT);
                  // execute call need communication with storage server.
                  stmt.execute(query);
                  LOG.debug("ConnPool GC: execut query, url=" + conn.getUrl() + ",query=" + query);
                } catch (SQLException e) {
                  // only handling the connection error
                  if ("08S01".equals(e.getSQLState())) {
                    // remove failed ConnNode in the ConnPool
                    sFreeList.remove(i);
                    sFreelistMaxSize.decrementAndGet();
                    LOG.info("ConnPool GC: Removing a failed connection (connId:" + conn.sConnId + ")");
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
                  }
                }
              }
              LOG.info("ConnPool GC: [" + connProp.connTypeName + "] ConnPool size after GC : " + sFreeList.size());
            } catch (InterruptedException e) {
              // Deliberately ignore
              interrupted = true;
            } catch (IndexOutOfBoundsException e) {
              // just ignore
            } catch (Exception e) {
              // just ignore
              LOG.error("ConnPool GC: [" + connProp.connTypeName + "] GC thread error :" + e.getMessage(), e);
            }
          }
        } finally {
          if (interrupted) {
            Thread.currentThread().interrupt();
          }
        }
      }
    };
    
    private CORE_RESULT initialize(String aConnType,
                                  ConnProperty.protocolType aProtoType) {
      // get properties for initialize ConnMgr
      final int initSize = QueryCacheServer.conf.getInt(
          QCConfigKeys.QC_CONNECTIONPOOL_FREE_INIT_SIZE,
          QCConfigKeys.QC_CONNECTIONPOOL_FREE_INIT_SIZE_DEFAULT);
      this.sFreelistThreshold = QueryCacheServer.conf.getFloat(
          QCConfigKeys.QC_CONNECTIONPOOL_FREE_RESIZING_F,
          QCConfigKeys.QC_CONNECTIONPOOL_FREE_RESIZING_F_DEFAULT);
      this.sResizingCycle = QueryCacheServer.conf.getLong(
          QCConfigKeys.QC_CONNECTIONPOOL_RESIZING_CYCLE_MILLI,
          QCConfigKeys.QC_CONNECTIONPOOL_RESIZING_CYCLE_MILLI_DEFAULT);
      this.sGCCycle = QueryCacheServer.conf.getLong(
          QCConfigKeys.QC_CONNECTIONPOOL_GC_CYCLE_MILLI,
          QCConfigKeys.QC_CONNECTIONPOOL_GC_CYCLE_MILLI_DEFAULT);
        
      if (connProp.setConnProperties(aConnType, aProtoType) != CORE_RESULT.CORE_SUCCESS)
        return CORE_RESULT.CORE_FAILURE;
      String url = "";
      LOG.info("Initializing the connection pool of " + aConnType + "...");
      // TODO: How can we handle url kv options? just ignore these?
      int numofRealConn = 0;
      for (int i = 0; i < initSize; i++) {
        try {
          ConnNode sConn = newConnNode();
          if (sConn == null) {
            continue;
          } else {
            ++numofRealConn;
          }
        } catch (LinkageError e) {
          LOG.error( "connection init error : " + connProp.connTypeName + " : " + e.getMessage(), e);
          return CORE_RESULT.CORE_FAILURE;
        } catch (ClassNotFoundException e) {
          LOG.error( "connection init error : " + connProp.connTypeName + " : " + e.getMessage(), e);
          return CORE_RESULT.CORE_FAILURE;
        }
      }

      // start background thread for checking connpool size and resizing if necessary
      new Thread(sReloadThread).start();
      if (QueryCacheServer.conf.getBoolean(QCConfigKeys.QC_CONNECTIONPOOL_GC_PREFIX +
           "." + aConnType, QCConfigKeys.QC_CONNECTIONPOOL_GC_DEFAULT)) {
        new Thread(sGCThread).start();
      }
      return CORE_RESULT.CORE_SUCCESS;
    }

    private ConnNode getConn(long aConnId) {
      return sUsingMap.get(aConnId);
    }

    private void removeConn(long aConnId) {
      ConnNode sConn = sUsingMap.remove(aConnId);
      sFreelistMaxSize.decrementAndGet();
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
      ConnNode sConn = null;
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
        String url = "";
        for (int i = 0; i < retryCnt; i++) {
          try {
            ConnNode sConn2 = newConnNode();
            if (sConn == null) {
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
        LOG.trace("Moving ConnNode from FreeList to UsingMap. -Type: {}, -Free size: {}, -FreeMaxSize:{}, -Using size:{}",
                connProp.connTypeName, sFreeList.size(),
                sFreelistMaxSize, sUsingMap.size());
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
      LOG.trace("Moving ConnNode from UsingMap to FreeList. -Free size:{}, -FreeMaxSize:{}, -Using size:{}",
              sFreeList.size(), sFreelistMaxSize, sUsingMap.size());
      return CORE_RESULT.CORE_SUCCESS;
    }
    
    private String buildUrl(boolean isPhoenix) {
      short sInd = 0;
      String sUrl = "";
      if (isPhoenix) {
        sUrl = connProp.connUrlPrefix;
        for (short i = 0; i < connProp.connAddr.length; i++) {
          sUrl += connProp.connAddr[i];
          if (i != connProp.connAddr.length - 1)
            sUrl += ',';
        }
        sUrl += ":" + connProp.connPort;
      } else {
        synchronized (this) {
          sInd = (connProp.connAddr.length - 1 <= connAddrIndex) ? connAddrIndex = 0
              : ++connAddrIndex;
        }
        sUrl = connProp.connUrlPrefix + connProp.connAddr[sInd] + ":"
            + connProp.connPort;
      }
      if (!connProp.connUrlSuffix.isEmpty())
        sUrl += "/" + connProp.connUrlSuffix;
      return sUrl;
    }

    public int getFreeConnCount() {
      return sFreeList.size();
    }
    public int getUsingConnCount() {
      return sUsingMap.size();
    }
  }

  // This Array contains ConnMgrofOnes of all of connection types.
  private Map<String, ConnMgrofOne> connMgrofAll = new HashMap<String, ConnMgrofOne>();
  // This contains running and completed query's profiling data
  public RuntimeProfile runtimeProfile = RuntimeProfile.getInstance();

  CORE_RESULT initialize() {
    String[] sDrivers = QueryCacheServer.conf.getStrings(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS);
    // initialize jdbc connpools
    for (String driver: sDrivers) {
      ConnMgrofOne sConn = new ConnMgrofOne(driver);
      if (sConn.initialize(driver, ConnProperty.protocolType.JDBC)
            == CORE_RESULT.CORE_FAILURE)
        continue;

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
      
      connMgrofAll.put("jdbc:" + driver, sConn);
    }
    
    // initialize hbase connpool
    String sHBaseDriver = QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_HBASE_DRIVER);
    
    if (sHBaseDriver != null) {
      ConnMgrofOne sConn = new ConnMgrofOne(sHBaseDriver);
      if (sConn.initialize(sHBaseDriver, ConnProperty.protocolType.HBASE)
            == CORE_RESULT.CORE_FAILURE) {
        LOG.error("Connection error to " + sHBaseDriver);
      } else {
        connMgrofAll.put("hbase:" + sHBaseDriver, sConn);
      }
    }
    
    return CORE_RESULT.CORE_SUCCESS;
  }

  public ConnNode getConn(String aType, long aConnId) {
    ConnMgrofOne sConnMgr = connMgrofAll.get(aType);
    if (sConnMgr == null) {
      return null;
    }
    return sConnMgr.getConn(aConnId);
  }

  public void removeConn(String aType, long aConnId) {
    ConnMgrofOne sConnMgr = connMgrofAll.get(aType);
    if (sConnMgr == null)
      return;
    sConnMgr.removeConn(aConnId);
  }
  
  public ConnNode allocConn(String aType) {
    ConnMgrofOne sConnMgr = connMgrofAll.get(aType);
    if (sConnMgr == null) {
      return null;
    }
    return sConnMgr.allocConn();
  }

  public CORE_RESULT closeConn(String aType, long aConnId) {
    ConnMgrofOne sConnMgr = connMgrofAll.get(aType);
    if (sConnMgr == null) {
      return null;
    }
    return sConnMgr.closeConn(aConnId);
  }
  
  public AuthorizationLoader getAuthLoader(String aType) {
    ConnMgrofOne sConnMgr = connMgrofAll.get(aType);
    if (sConnMgr == null) {
      return null;
    }
    return sConnMgr.authLoader;
  }
  
  public Map<String, ConnMgrofOne> getConnMgrofAll() {
    return this.connMgrofAll;
  }
}
