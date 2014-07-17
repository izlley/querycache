package com.skplanet.querycache.server;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import com.skplanet.querycache.server.util.ObjectPool.TargetObjs;

public class ConnMgr {
  private static final Logger LOG = LoggerFactory.getLogger(ConnMgr.class);

  private class ConnMgrofOne {
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

    // TODO: use hashcode instead of long
    // If it overflows, it goes back to the minimum value and continues from
    // there.
    // Long.MAX_VALUE = 9223372036854775807
    private final AtomicLong sConnIdGen = new AtomicLong(0L);
    private float sFreelistThreshold; // def:0.2f
    private AtomicLong sFreelistMaxSize; // def:16
    private long sResizingCycle; // def:15*1000
    private long sGCCycle; // def:5*60*1000;

    // below properties are used for connecting to storage system
    private ConnProperty connProp = new ConnProperty();
    // index of connection addresses for failover control in a round-robin fashion
    private short connAddrIndex = -1;
    
    // managing authorization control
    private AuthorizationLoader authLoader = null;
    
    // It's a checker thread running in the background to extend the size of the ConnPool.
    private Runnable sReloadThread = new Runnable() {
      public void run() {
        LOG.info("ConnPool resizing thread init.");
        boolean interrupted = false;
        try {
          while (true) {
            try {
              Thread.sleep(sResizingCycle);
              int currSize = sFreeList.size();
              long maxSize = sFreelistMaxSize.get();
              int  addedCnt = 0;
              String url = "";
              if (currSize < (int)(maxSize * sFreelistThreshold)) {
                LOG.info("Resizing more ConnNode in the ConnPool, Type = "
                  + connProp.connTypeName + ", current size = "
                  + currSize + ", max size = " + maxSize);
                // resizing connpool to x2
                for (int i = 0; i < maxSize; i++) {
                  ConnNode sConn = new ConnNode();
                  try {
                    // Every time builUrl is called, returns different address for distributing connections
                    url = buildUrl();
                    sConn.initialize(connProp,
                                     sConnIdGen.addAndGet(1L),
                                     url);
                    LOG.info("Add " + (addedCnt + 1) + "st ConnNode for ConnPool to " + url);
                    // append to the FreeList : O(1)
                    sFreeList.add(sConn);
                    sFreelistMaxSize.addAndGet(1);
                    addedCnt++;
                  } catch (SQLException e) {
                    LOG.error(
                      "Connection error to (" + url + ") " + ":" + e.getMessage(), e);
                    // I don't wanna infinite loop here.
                    //i--;
                  } catch (LinkageError e) {
                    LOG.error(
                      "Connection init error " + connProp.connTypeName + ":" + e.getMessage(), e);
                  } catch (ClassNotFoundException e) {
                    LOG.error(
                      "Connection init error " + connProp.connTypeName + ":" + e.getMessage(), e);
                  }
                }
                LOG.info("FreeList resizing from " + maxSize + " to "
                    + sFreelistMaxSize.get());
              }
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
    
    // It's a checker thread running in the background to detect failed connections 
    // and remove those connections.
    // TODO: If a client abort a connection abnormally, the qc server does not know 
    //   whether it's a normal close case or not. Therefore, this connection is retained forever.
    //   We also need gc for removing these zombie connections periodically.
    private Runnable sGCThread = new Runnable() {
      public void run() {
        LOG.info("ConnPool GC thread init.");
        boolean interrupted = false;
        try {
          while (true) {
            try {
              Thread.sleep(sGCCycle);
              LOG.info("[" + connProp.connTypeName + "] ConnPool GC activated...");
              // is this thread-safe? it doesn't matter
              for (Iterator<ConnNode> iter = sFreeList.iterator(); iter.hasNext();) {
                Statement stmt = null;
                ConnNode conn = iter.next();
                try {
                  stmt = conn.sHConn.createStatement();
                  // execute call need communication with storage server.
                  stmt.execute(QueryCacheServer.conf.get(
                      QCConfigKeys.QC_CONNECTIONPOOL_GC_VERIFY_QUERY,
                      QCConfigKeys.QC_CONNECTIONPOOL_GC_VERIFY_QUERY_DEFAULT));
                } catch (SQLException e) {
                  // only handling the connection error
                  if (e.getSQLState().equals("08S01")) {
                    // remove failed ConnNode in the ConnPool
                    iter.remove();
                    LOG.info("ConnPool GC: Removing a failed connection (connId:" + conn.sConnId + ")");
                  }
                } finally {
                  try {
                    stmt.close();
                  } catch (SQLException e) {
                    // just ignore
                    LOG.warn("ConnPool GC: closing stmt error (connId:" + conn.sConnId + ")" +
                      ", " + e.getMessage());
                  }
                }
              }
              LOG.info("[" + connProp.connTypeName + "] ConnPool size after GC : " + sFreeList.size());
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
      
      // TODO: How can we handle url kv options? just ignore these?
      for (int i = 0; i < initSize; i++) {
        ConnNode sConn = new ConnNode();
        try {
          // Every time builUrl is called, returns different address for distributing connections
          url = buildUrl();
          sConn.initialize(connProp,
                           sConnIdGen.addAndGet(1L),
                           url);
          LOG.info("Add " + (i + 1) + "st ConnNode for ConnPool to " + url);
          // append to the FreeList : O(1)
          sFreeList.add(sConn);
        } catch (SQLException e) {
          LOG.error("Connection error to (" + url + ") " + ":" + e.getMessage(), e);
          LOG.info("Retrying connection");
          i--;
          continue;
        } catch (LinkageError e) {
          LOG.error(
            "Connection init error " + aConnType + ":" + e.getMessage(), e);
          return CORE_RESULT.CORE_FAILURE;
        } catch (ClassNotFoundException e) {
          LOG.error(
            "Connection init error " + aConnType + ":" + e.getMessage(), e);
          return CORE_RESULT.CORE_FAILURE;
        }
      }
      sFreelistMaxSize = new AtomicLong(initSize);

      // start background thread for checking connpool size and resizing if necessary
      new Thread(sReloadThread).start();
      if (QueryCacheServer.conf.getBoolean(QCConfigKeys.QC_CONNECTIONPOOL_GC_PREFIX +
           "." + aConnType, QCConfigKeys.QC_CONNECTIONPOOL_GC_DEFAULT)) {
        new Thread(sGCThread).start();
      }
      return CORE_RESULT.CORE_SUCCESS;
    }

    private ConnNode getConn(long aConnId) {
      // return null if this map contains no mapping for the key
      // O(1)
      ConnNode sConn = sUsingMap.get(aConnId);
      if (sConn == null) {
        LOG.warn(
          "ConnMgrofOne.getConn(): There is no connection in ConnPool mapping to the id"
          + "(" + aConnId + ")");
      }
      return sConn;
    }

    private void removeConn(long aConnId) {
      // return null if this map contains no mapping for the key
      // O(1)
      ConnNode sConn = sUsingMap.remove(aConnId);
      if (sConn == null) {
        LOG.warn(
          "ConnMgrofOne.removeConn(): There is no connection in ConnPool mapping to the id"
          + "(" + aConnId + ")");
      }
    }
    
    private ConnNode allocConn() {
      // 1. get a ConNode from the FreeList
      // O(1)
      ConnNode sConn = sFreeList.remove(0);
      
      // 2. if no ConNode in the ConnPool, make connection explicitly
      if (sConn == null) {
        // retry count is not configurable
        short retryCnt = 3;
        String url = "";
        for (int i = 0; i < retryCnt; i++) {
          ConnNode sConn2 = new ConnNode();
          try {
            // Every time builUrl is called, it returns different address for distributing connections
            url = buildUrl();
            sConn.initialize(connProp,
                             sConnIdGen.addAndGet(1L),
                             url);
            LOG.info("Add " + (i + 1) + "st ConnNode for ConnPool to " + url);
            // append to the FreeList : O(1)
            sFreelistMaxSize.addAndGet(1);
            sConn = sConn2;
            break;
          } catch (SQLException e) {
            LOG.error("Connection error to (" + url + ") " + ":" + e.getMessage(), e);
            LOG.info("Retrying connection");
            continue;
          } catch (LinkageError e) {
            LOG.error(
              "Connection init error " + connProp.connTypeName + ":" + e.getMessage(), e);
            LOG.info("Retrying connection");
            continue;
          } catch (ClassNotFoundException e) {
            LOG.error(
              "Connection init error " + connProp.connTypeName + ":" + e.getMessage(), e);
            LOG.info("Retrying connection");
            continue;
          }
        }
      }
      
      if (sConn != null) {
        // O(1)
        sUsingMap.put(sConn.sConnId, sConn);
        LOG.info("Moving ConnNode from FreeList to UsingMap. -Type: "
            + connProp.connTypeName + "-Free size:"
            + sFreeList.size() + ",-FreeMaxSize:" + sFreelistMaxSize
            + ",-Using size:" + sUsingMap.size());
      }

      return sConn;
    }
    
    private CORE_RESULT closeConn(long aConnId) {
      ConnNode sConn = sUsingMap.remove(aConnId);
      
      // close all statements in the ConnNode
      try {
        sConn.closeAllStmts();
      } catch (SQLException e1) {
        LOG.error("Statement close error :" + e1.getMessage(), e1);
        // close this connection forcely
        try {
          sConn.sHConn.close();
        } catch (SQLException e2) {
          LOG.error("Connection close error :" + e2.getMessage(), e2);
        }
        sConn.finalize();
        return CORE_RESULT.CORE_FAILURE;
      }

      // add ConnNode to FreeList
      sFreeList.add(sConn);
      LOG.info("Moving ConnNode from UsingMap to FreeList." + "-Free size:"
          + sFreeList.size() + ",-FreeMaxSize:" + sFreelistMaxSize
          + ",-Using size:" + sUsingMap.size());
      return CORE_RESULT.CORE_SUCCESS;
    }
    
    private String buildUrl() {
      short sInd = 0;
      synchronized(this){
        sInd = (connProp.connAddr.length - 1 <= connAddrIndex)? 
          connAddrIndex = 0 : ++connAddrIndex;
      }
      String sUrl =
        connProp.connUrlPrefix +
        connProp.connAddr[sInd] +
        ":" + connProp.connPort;

      if (!connProp.connUrlSuffix.isEmpty()) {
        sUrl += "/" + connProp.connUrlSuffix;
      }
      return sUrl;
    }
  }

  // This Array contain ConnMgrofOnes of all of connection types.
  private Map<String, ConnMgrofOne> connMgrofAll;

  CORE_RESULT initialize() {
    connMgrofAll = new HashMap<String, ConnMgrofOne>();
    String[] sDrivers = QueryCacheServer.conf.getStrings(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS);
    // initialize jdbc connpools
    for (String driver: sDrivers) {
      ConnMgrofOne sConn = new ConnMgrofOne();
      if (sConn.initialize(driver, ConnProperty.protocolType.JDBC)
            == CORE_RESULT.CORE_FAILURE) {
        LOG.error("Connection error to " + driver);
        continue;
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
      
      connMgrofAll.put("jdbc:" + driver, sConn);
    }
    
    // initialize hbase connpool
    String sHBaseDriver = QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_HBASE_DRIVER);
    
    if (sHBaseDriver != null) {
      ConnMgrofOne sConn = new ConnMgrofOne();
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
