package com.skplanet.querycache.server.common;

public class Configure {
  // querycache server port
  static public final int gServerPort = 8655;
  // server handler num
  static public final int gMinWorkerThreads = 64;
  static public final int gMaxWorkerThreads = 16;
  
  // HBase ZK ip/port
  //static public final String gPhoenixZkqIp = "211.234.235.55";
  static public final String gPhoenixZkqIp = "DICm001";
  static public final String gPhoenixZkPort = "2181";
  
  // ConMgr properties
  static public final int gConnPoolFreeInitSize = 16;
  static public final float gConnPoolFreeResizingF = 0.2f;
  
  // for query profiling
  static public final boolean gQueryProfile = true;
  
  // ObjectPool
  static public final int gMaxPoolSize = 1024;
  static public final long gReloadCycle = 30 * 1000; //ms
  static public final float gObjPoolResizingF = 0.2f;
}
