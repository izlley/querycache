package com.skplanet.querycache.server.sqlcompiler;

import com.skplanet.querycache.server.common.QuerycacheException;

/**
 * Thrown for authorization errors encountered when accessing DB objects.
 */
public class AuthorizationException extends QuerycacheException {
  public AuthorizationException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public AuthorizationException(String msg) {
    super(msg);
  }
}
