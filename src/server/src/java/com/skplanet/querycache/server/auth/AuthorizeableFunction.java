package com.skplanet.querycache.server.auth;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Class used to authorize access to a table or view.
 * Even though Hive's spec includes an authorizable object 'view', we chose
 * to treat views the same way as tables for the sake of authorization.
 */
public class AuthorizeableFunction implements Authorizeable {
  private final org.apache.sentry.core.model.db.Function function_;
  private final org.apache.sentry.core.model.db.Database database_;

  public AuthorizeableFunction(String dbName, String functionName) {
    Preconditions.checkState(functionName != null && !functionName.isEmpty());
    Preconditions.checkState(dbName != null && !dbName.isEmpty());
    function_ = new org.apache.sentry.core.model.db.Function(functionName);
    database_ = new org.apache.sentry.core.model.db.Database(dbName);
  }

  @Override
  public List<org.apache.sentry.core.common.Authorizable> getHiveAuthorizeableHierarchy() {
    return Lists.newArrayList((org.apache.sentry.core.common.Authorizable) database_,
      (org.apache.sentry.core.common.Authorizable) function_);
  }

  @Override
  public String getName() {
    return database_.getName() + "." + function_.getName();
  }
}