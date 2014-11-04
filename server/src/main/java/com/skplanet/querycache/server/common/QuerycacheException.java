package com.skplanet.querycache.server.common;

public abstract class QuerycacheException extends java.lang.Exception {
  public QuerycacheException(String msg, Throwable cause) {
    super(msg, cause);
  }
  
  protected QuerycacheException(String msg) {
    super(msg);
  }
}
