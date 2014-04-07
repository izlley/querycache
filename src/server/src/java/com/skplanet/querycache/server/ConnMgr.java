package com.skplanet.querycache.server;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.common.Configure;
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

    CORE_RESULT initialize(int aInitSize, float aResizingF, ConnType aType) {
      sFreelistThreshold = aResizingF;

      String sUrl = buildUrl(aType);

      for (int i = 0; i < aInitSize; i++) {
        ConnNode sConn = new ConnNode();
        try {
          sConn.initialize(aType, sConnIdGen.addAndGet(1L), sUrl);
          LOG.info("Make " + (i + 1) + "st ConnNode for ConnPool of " + aType.name());
          // append to the FreeList : O(1)
          sFreeList.add(sConn);
        } catch (SQLException e) {
          LOG.error("Connection error " + aType.name() + ":" + e.getMessage(),
              e);
          return CORE_RESULT.CORE_FAILURE;
        } catch (LinkageError e) {
          LOG.error(
              "Connection init error " + aType.name() + ":" + e.getMessage(), e);
          return CORE_RESULT.CORE_FAILURE;
        } catch (ClassNotFoundException e) {
          LOG.error(
              "Connection init error " + aType.name() + ":" + e.getMessage(), e);
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

    public ConnNode allocConn(ConnType aType) {
      // 1. check the free-list whether it have available ConnNodes or not
      long sFreeMaxSize = sFreelistMaxSize.get();
      try {
        if (sFreeList.size() < sFreeMaxSize * sFreelistThreshold) {
          long sPostMaxSize;
          LOG.info("FreeList in ConnPool is too small, so resizing process is activated.");
          String sUrl = buildUrl(aType);
          for (int i = 0; i < sFreeMaxSize; i++) {
            ConnNode sConn = new ConnNode();
            sConn.initialize(aType, sConnIdGen.addAndGet(1L), sUrl);
            LOG.debug("Make " + (i + 1) + "st ConnNode for ConnPool.");
            // append to the FreeList : O(1)
            sFreeList.add(sConn);
          }
          sPostMaxSize = sFreelistMaxSize.addAndGet(sFreeMaxSize);
          LOG.info("FreeList resizing from " + sFreeMaxSize + " to "
              + sPostMaxSize);
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

    private String buildUrl(ConnType aType) {
      String sUrl = "";
      switch (aType) {
        case PHOENIX_JDBC:
          sUrl = "jdbc:phoenix:" + Configure.gPhoenixZkqIp + ":"
              + Configure.gPhoenixZkPort;
          break;
        case IMPALA_JDBC:
          sUrl = "jdbc:hive2://" + Configure.gImpalaIp + ":"
              + Configure.gImpalaPort + "/;auth=noSasl";
          break;
        case HIVE_JDBC:
          sUrl = "jdbc:hive2://" + Configure.gHiveIp + ":"
              + Configure.gHivePort;
          break;
          // below types are not supported yet
          // case IMPALA_THRIFT:
          // case HBASE:
          // case MYSQL_JDBC:
        default:
          break;
      }
      return sUrl;
    }
  }

  // This Array contain ConnMgrofOnes of all of connection types.
  ConnMgrofOne sConnMgrofAll[];

  CORE_RESULT initialize(int aInitSize, float aResizingF) {
    int sTypeLen = ConnType.values().length;
    this.sConnMgrofAll = new ConnMgr.ConnMgrofOne[sTypeLen];
    for (ConnType sType : ConnType.values()) {
      int sInd = sType.getIndex();
      switch (sType) {
        case PHOENIX_JDBC:
        case IMPALA_JDBC:
        case HIVE_JDBC:
          sConnMgrofAll[sInd] = new ConnMgrofOne();
          if (sConnMgrofAll[sInd].initialize(aInitSize, aResizingF, sType) == CORE_RESULT.CORE_FAILURE) {
            return CORE_RESULT.CORE_FAILURE;
          }
          break;
        default:
          break;
      }
    }
    return CORE_RESULT.CORE_SUCCESS;
  }

  public ConnNode getConn(ConnType aType, long aConnId) {
    if (!CheckConnType(aType)) {
      return null;
    }
    return sConnMgrofAll[aType.getIndex()].getConn(aConnId);
  }

  public ConnNode allocConn(ConnType aType) {
    if (!CheckConnType(aType)) {
      return null;
    }
    return sConnMgrofAll[aType.getIndex()].allocConn(aType);
  }

  public CORE_RESULT closeConn(ConnType aType, long aConnId) {
    if (!CheckConnType(aType)) {
      return CORE_RESULT.CORE_SUCCESS;
    }
    return sConnMgrofAll[aType.getIndex()].closeConn(aConnId);
  }

  private boolean CheckConnType(ConnType aType) {
    boolean isType = false;
    switch (aType) {
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
  }
}
