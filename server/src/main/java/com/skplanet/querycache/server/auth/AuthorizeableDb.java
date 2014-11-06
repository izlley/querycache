package com.skplanet.querycache.server.auth;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/*
 * Class used to authorize access to a database.
 */
public class AuthorizeableDb implements Authorizeable {
  private final org.apache.sentry.core.model.db.Database database_;
  
  public AuthorizeableDb(String dbName) {
    Preconditions.checkState(dbName != null && !dbName.isEmpty());
    database_ = new org.apache.sentry.core.model.db.Database(dbName);
  }

  @Override
  public List<org.apache.sentry.core.common.Authorizable> getHiveAuthorizeableHierarchy() {
    return Lists.newArrayList((org.apache.sentry.core.common.Authorizable) database_);
  }
  @Override
  public String getName() { 
    return database_.getName();
  }
}
