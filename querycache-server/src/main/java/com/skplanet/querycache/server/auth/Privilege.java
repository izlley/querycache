package com.skplanet.querycache.server.auth;

import java.util.EnumSet;
import org.apache.sentry.core.model.db.DBModelAction;

/*
 * Maps an Impala Privilege to one or more Hive Access "Actions".
 */
public enum Privilege {
  ALL(DBModelAction.ALL, false),
  ALTER(DBModelAction.ALL, false),
  DROP(DBModelAction.ALL, false),
  CREATE(DBModelAction.ALL, false),
  INSERT(DBModelAction.INSERT, false),
  SELECT(DBModelAction.SELECT, false),
  // Privileges required to view metadata on a server object.
  VIEW_METADATA(EnumSet.of(DBModelAction.INSERT, DBModelAction.SELECT), true),
  // Special privilege that is used to determine if the user has any valid privileges
  // on a target object.
  ANY(EnumSet.allOf(DBModelAction.class), true),
  ;

  private final EnumSet<DBModelAction> actions_;

  // Determines whether to check if the user has ANY the privileges defined in the
  // actions list or whether to check if the user has ALL of the privileges in the
  // actions list.
  private final boolean anyOf_;

  private Privilege(EnumSet<DBModelAction> actions, boolean anyOf) {
    actions_ = actions;
    anyOf_ = anyOf;
  }

  private Privilege(DBModelAction action, boolean anyOf) {
    this(EnumSet.of(action), anyOf);
  }

  /*
   * Returns the set of Hive Access Actions mapping to this Privilege.
   */
  public EnumSet<DBModelAction> getHiveActions() { return actions_; }

  /*
   * Determines whether to check if the user has ANY the privileges defined in the
   * actions list or whether to check if the user has ALL of the privileges in the
   * actions list.
   */
  public boolean getAnyOf() { return anyOf_; }
}
