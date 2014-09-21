package com.skplanet.querycache.server;

import com.skplanet.querycache.server.common.InternalType.CORE_RESULT;

public class ConnProperty {
  public String connTypeName  = "";
  public String connPkgPath   = "";
  public String[] connAddr    = {};
  public String connPort      = "";
  public String connUserId    = null;
  public String connPass      = null;
  public String connUrlPrefix = "";
  public String connUrlSuffix = "";
  public protocolType protoType;
  
  public static enum protocolType {
    JDBC, HBASE, 
  }
  
  CORE_RESULT setConnProperties(String aConnType, ConnProperty.protocolType aProtoType) {
    this.connTypeName = aConnType;
    this.protoType = aProtoType;
    this.connPkgPath =
        QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS_PKGPATH_PREFIX + '.' + aConnType, "");
    this.connAddr =
        QueryCacheServer.conf.getStrings(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS_ADDRESS_PREFIX + '.' + aConnType, new String[0]);
    this.connPort =
        QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS_PORT_PREFIX + '.' + aConnType, "");
    this.connUserId =
        QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS_USER_PREFIX + '.' + aConnType, null);
    this.connPass =
        QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS_PASSWORD_PREFIX + '.' + aConnType, null);
    this.connUrlPrefix =
        QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS_URLPREFIX_PREFIX + '.' + aConnType, "");
    this.connUrlSuffix =
        QueryCacheServer.conf.get(QCConfigKeys.QC_STORAGE_JDBC_DRIVERS_URLSUFFIX_PREFIX + '.' + aConnType, "");
    if (this.connTypeName.isEmpty() ||
        this.connPkgPath.isEmpty() ||
        this.connAddr.length == 0 ||
        this.connUrlPrefix.isEmpty()) {
      return CORE_RESULT.CORE_FAILURE;
    }
    return CORE_RESULT.CORE_SUCCESS;
  }
}
