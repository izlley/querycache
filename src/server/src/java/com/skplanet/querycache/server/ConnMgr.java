package com.skplanet.querycache.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.common.InternalType.*;

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
    List<ConnNode> sFreeList = Collections
        .synchronizedList(new LinkedList<ConnNode>());
    // TODO: We need a periodic garbage collection of zombie connections to
    // prevent unintended
    // UsingMap expansion.
    ConcurrentHashMap<Long, ConnNode> sUsingMap = new ConcurrentHashMap<Long, ConnNode>(
        16, 0.9f, 1);

    // TODO: use hashcode instead of long
    // If it overflows, it goes back to the minimum value and continues from
    // there.
    // Long.MAX_VALUE = 9223372036854775807
    private final AtomicLong sConnIdGen = new AtomicLong(0L);
    private float sFreelistThreshold; // def:0.2f
    private AtomicLong sFreelistMaxSize; // def:16

    // below properties are used for connecting to storage system
    ConnProperty connProp = new ConnProperty();
    
    CORE_RESULT initialize(int aInitSize, float aResizingF, String aConnType,
        ConnProperty.protocolType aProtoType) {
      sFreelistThreshold = aResizingF;
      if (connProp.setConnProperties(aConnType, aProtoType) != CORE_RESULT.CORE_SUCCESS)
        return CORE_RESULT.CORE_FAILURE;

      // TODO: How can we handle url kv options? just ignore these?
      String sUrl = buildUrl();

      for (int i = 0; i < aInitSize; i++) {
        ConnNode sConn = new ConnNode();
        try {
          sConn.initialize(connProp,
                           sConnIdGen.addAndGet(1L),
                           sUrl);
          LOG.info("Make " + (i + 1) + "st ConnNode for ConnPool of " + aConnType);
          // append to the FreeList : O(1)
          sFreeList.add(sConn);
        } catch (SQLException e) {
          LOG.error("Connection error " + aConnType + ":" + e.getMessage(),
              e);
          return CORE_RESULT.CORE_FAILURE;
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
      sFreelistMaxSize = new AtomicLong(aInitSize);

      return CORE_RESULT.CORE_SUCCESS;
    }

    public ConnNode getConn(long aConnId) {
      // return null if this map contains no mapping for the key
      // O(1)
      ConnNode sConn = sUsingMap.get(aConnId);
      if (sConn == null) {
        LOG.warn("There is no connection in ConnPool mapping to the id" + "("
            + aConnId + ")");
      }
      return sConn;
    }

    public ConnNode allocConn() {
      // 1. check the free-list whether it have available ConnNodes or not
      try {
        synchronized(this) {
          long sFreeMaxSize = sFreelistMaxSize.get();
          if (sFreeList.size() < sFreeMaxSize * sFreelistThreshold) {
            long sPostMaxSize;
            LOG.info("FreeList in ConnPool is too small, so resizing process is activated.");
            String sUrl = buildUrl();
            for (int i = 0; i < sFreeMaxSize; i++) {
              ConnNode sConn = new ConnNode();
              sConn.initialize(connProp, sConnIdGen.addAndGet(1L), sUrl);
              LOG.debug("Make " + (i + 1) + "st ConnNode for ConnPool.");
              // append to the FreeList : O(1)
              sFreeList.add(sConn);
            }
            sPostMaxSize = sFreelistMaxSize.addAndGet(sFreeMaxSize);
            LOG.info("FreeList resizing from " + sFreeMaxSize + " to "
                + sPostMaxSize);
          }
        }
        // just ignore these exceptions
      } catch (SQLException e) {
        LOG.warn("Connection error :" + e.getMessage(), e);
      } catch (LinkageError e) {
        LOG.error("Connection init error :" + e.getMessage(), e);
      } catch (ClassNotFoundException e) {
        LOG.error("Connection init error :" + e.getMessage(), e);
      }

      // 2. move from FreeList to UsingMap
      // O(1)
      ConnNode sConn = sFreeList.remove(0);
      if (sConn != null) {
        // O(1)
        sUsingMap.put(sConn.sConnId, sConn);
        LOG.info("Moving ConnNode from FreeList to UsingMap." + "-Free size:"
            + sFreeList.size() + ",-FreeMaxSize:" + sFreelistMaxSize
            + ",-Using size:" + sUsingMap.size());
      }

      return sConn;
    }

    public CORE_RESULT closeConn(long aConnId) {
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
      String sUrl = "";
      sUrl = connProp.connUrlPrefix + connProp.connAddr;

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
    
    // initialize jdbc connpools
    String[] sDrivers = QueryCacheServer.conf.getStrings(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS);
    
    for (String driver: sDrivers) {
      ConnMgrofOne sConn = new ConnMgrofOne();
      if (sConn.initialize(
          QueryCacheServer.conf.getInt(QCConfigKeys.QC_CONNECTIONPOOL_FREE_INIT_SIZE,
            QCConfigKeys.QC_CONNECTIONPOOL_FREE_INIT_SIZE_DEFAULT),
          QueryCacheServer.conf.getFloat(QCConfigKeys.QC_CONNECTIONPOOL_FREE_RESIZING_F,
            QCConfigKeys.QC_CONNECTIONPOOL_FREE_RESIZING_F_DEFAULT),
          driver,
          ConnProperty.protocolType.JDBC) == CORE_RESULT.CORE_FAILURE) {
        LOG.error("Connection error to " + driver);
        continue;
      }
      connMgrofAll.put("jdbc:" + driver, sConn);
    }
    
    // initialize hbase connpool
    String sHBaseDriver = QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_HBASE_DRIVER);
    
    if (sHBaseDriver != null) {
      ConnMgrofOne sConn = new ConnMgrofOne();
      if (sConn.initialize(
          QueryCacheServer.conf.getInt(QCConfigKeys.QC_CONNECTIONPOOL_FREE_INIT_SIZE,
            QCConfigKeys.QC_CONNECTIONPOOL_FREE_INIT_SIZE_DEFAULT),
          QueryCacheServer.conf.getFloat(QCConfigKeys.QC_CONNECTIONPOOL_FREE_RESIZING_F,
            QCConfigKeys.QC_CONNECTIONPOOL_FREE_RESIZING_F_DEFAULT),
          sHBaseDriver,
          ConnProperty.protocolType.HBASE) == CORE_RESULT.CORE_FAILURE) {
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

  /*private boolean CheckConnType(String aType) {
    boolean isType = false;
    switch (aType) {
      case MYSQL_JDBC:
        LOG.error("Connection type is unknown : [" + aType.getIndex() + "]");
        break;
      case PHOENIX_JDBC:
      case IMPALA_JDBC:
      case HIVE_JDBC:
        isType = true;
        break;
      default:
        LOG.error("Connection type is unknown : [" + aType.getIndex() + "]");
        break;
    }
    return isType;
  }*/
}
