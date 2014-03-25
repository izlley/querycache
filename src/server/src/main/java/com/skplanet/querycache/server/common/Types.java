package com.skplanet.querycache.server.common;

import com.skplanet.querycache.thrift.TTypeId;

public class Types {
  static public enum ConnType {
    UNKNOWN,
    PHOENIX_JDBC,
    IMPALA_JDBC,
    IMPALA_THRIFT,
    HIVE_JDBC,
    HBASE,
    MYSQL_JDBC,
  }
  
  static public enum CORE_RESULT {
    CORE_SUCCESS,
    CORE_SUCCESS_WITH_INFO,
    CORE_FAILURE,
  }
}
