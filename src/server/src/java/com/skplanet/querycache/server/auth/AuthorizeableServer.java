package com.skplanet.querycache.server.auth;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class AuthorizeableServer implements Authorizeable {
  // server distinguish datastorage systems (e.g. hive, impala, hbase ...)
  private final org.apache.sentry.core.model.db.Server server_;
  
  public AuthorizeableServer(String serverName) {
    Preconditions.checkState(serverName != null && !serverName.isEmpty());
    server_ = new org.apache.sentry.core.model.db.Server(serverName);
  }

  @Override
  public List<org.apache.sentry.core.common.Authorizable> getHiveAuthorizeableHierarchy() {
    return Lists.newArrayList((org.apache.sentry.core.common.Authorizable) server_);
  }

  @Override
  public String getName() { return server_.getName(); }
}
