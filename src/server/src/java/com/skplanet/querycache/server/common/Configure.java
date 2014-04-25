package com.skplanet.querycache.server.common;

public class Configure {
  // querycache server port
  static public final int gServerPort = 8655;
  // server handler num
  static public final int gMinWorkerThreads = 16;
  static public final int gMaxWorkerThreads = 64;
  
  // HBase ZK ip/port
  //static public final String gPhoenixZkqIp = "211.234.235.55";
  static public final String gPhoenixZkqIp = "DICm001";
  static public final String gPhoenixZkPort = "2181";
  
  // Impala cluster ip/port
  static public final String gImpalaIp = "DICn010";
  //static public final String gImpalaIp = "localhost";
  static public final String gImpalaPort = "21050";
  
  // Hive server ip/port
  static public final String gHiveIp = "DICm003";
  //static public final String gHiveIp = "localhost";
  static public final String gHivePort = "10000";
  
  // MySQL server ip/port
  static public final String gMysqlIp = "172.19.109.238";
  //static public final String gMysqlIp = "localhost";
  static public final String gMysqlPort = "3306";
  static public final String gMysqlUser = "hive";
  //static public final String gMysqlPass = "hive";
  static public final String gMysqlPass = "!had.litc4#";
  
  // ConMgr properties
  static public final int gConnPoolFreeInitSize = 16;
  static public final float gConnPoolFreeResizingF = 0.2f;
  
  // for query profiling
  static public final boolean gQueryProfile = true;
  
  // ObjectPool
  static public final int gMaxPoolSize = 1024 * 1024;
  static public final long gReloadCycle = 15 * 1000; //ms
  static public final float gObjPoolResizingF = 0.2f;
}
