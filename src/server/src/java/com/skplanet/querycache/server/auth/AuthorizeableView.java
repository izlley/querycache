package com.skplanet.querycache.server.auth;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class AuthorizeableView implements Authorizeable {
  // Constant to represent privileges in the policy for "ANY" table in a
  // a database.
  public final static String ANY_TABLE_NAME = org.apache.sentry.core.model.db.AccessConstants.ALL;

  private final org.apache.sentry.core.model.db.View view_;
  private final org.apache.sentry.core.model.db.Database database_;

  public AuthorizeableView(String dbName, String viewName) {
    Preconditions.checkState(viewName != null && !viewName.isEmpty());
    Preconditions.checkState(dbName != null && !dbName.isEmpty());
    view_ = new org.apache.sentry.core.model.db.View(viewName);
    database_ = new org.apache.sentry.core.model.db.Database(dbName);
  }

  @Override
  public List<org.apache.sentry.core.common.Authorizable> getHiveAuthorizeableHierarchy() {
    return Lists.newArrayList((org.apache.sentry.core.common.Authorizable) database_,
      (org.apache.sentry.core.common.Authorizable) view_);
  }

  @Override
  public String getName() {
    return database_.getName() + "." + view_.getName();
  }
}