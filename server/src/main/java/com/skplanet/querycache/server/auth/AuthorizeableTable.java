package com.skplanet.querycache.server.auth;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Class used to authorize access to a table or view.
 * Even though Hive's spec includes an authorizable object 'view', we chose
 * to treat views the same way as tables for the sake of authorization.
 */
public class AuthorizeableTable implements Authorizeable {
  // Constant to represent privileges in the policy for "ANY" table in a
  // a database.
  public final static String ANY_TABLE_NAME = org.apache.sentry.core.model.db.AccessConstants.ALL;

  private final org.apache.sentry.core.model.db.Table table_;
  private final org.apache.sentry.core.model.db.Database database_;

  public AuthorizeableTable(String dbName, String tableName) {
    Preconditions.checkState(tableName != null && !tableName.isEmpty());
    Preconditions.checkState(dbName != null && !dbName.isEmpty());
    table_ = new org.apache.sentry.core.model.db.Table(tableName);
    database_ = new org.apache.sentry.core.model.db.Database(dbName);
  }

  @Override
  public List<org.apache.sentry.core.common.Authorizable> getHiveAuthorizeableHierarchy() {
    return Lists.newArrayList((org.apache.sentry.core.common.Authorizable) database_,
      (org.apache.sentry.core.common.Authorizable) table_);
  }

  @Override
  public String getName() {
    return database_.getName() + "." + table_.getName();
  }
}