package com.skplanet.querycache.server.auth;

import java.util.List;

/*
 * Interface all authorizeable objects (Table, Db, Column, etc) must implement.
 */
public interface Authorizeable {
  /*
  * Returns the list of the Hive "authorizeable" objects in their hierarchical order.
  * For example:
  * [Column] would return Db -> Table -> Column
  * [Table] would return Db -> Table
  * [Db] would return [Db]
  * [URI] would return [URI]
  */
  public List<org.apache.sentry.core.common.Authorizable> getHiveAuthorizeableHierarchy();

  // Returns the name of the object.
  public String getName();
}