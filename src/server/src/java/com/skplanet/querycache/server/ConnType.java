package com.skplanet.querycache.server;

public enum ConnType {
  PHOENIX_JDBC {
    int getIndex() { return 0; }
    String getPackgePath() {
      return "org.apache.phoenix.jdbc.PhoenixDriver";
    }
  },
  IMPALA_JDBC{
    int getIndex() { return 1; }
    String getPackgePath() {
      return "org.apache.hive.jdbc.HiveDriver";
    }
  },
  HIVE_JDBC{
    int getIndex() { return 2; }
    String getPackgePath() {
      return "org.apache.hive.jdbc.HiveDriver";
    }
  },
  UNKNOWN {
    int getIndex() { return -1; }
    String getPackgePath() {
      return "";
    }
  };
  // below types are not supported yet
  //IMPALA_THRIFT(""),
  //MYSQL_JDBC(""),
  //HBASE("");
  
  abstract int getIndex();
  abstract String getPackgePath();
}
