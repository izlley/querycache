package com.skplanet.querycache.server.common;

public class Configure {
  // querycache server port
  static public final int gServerPort = 8282;
  
  // HBase ZK ip/port
  static public final String gPhoenixZkqIp = "211.234.235.55";
  static public final String gPhoenixZkPort = "2181";
  
  // ConMgr properties
  static public final int gConnPoolFreeInitSize = 16;
  static public final float gConnPoolFreeResizingF = 0.2f;
}
