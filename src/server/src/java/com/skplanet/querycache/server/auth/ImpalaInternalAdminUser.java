package com.skplanet.querycache.server.auth;

/*
 * A singleton class that represents a special user type used for internal Impala
 * sessions (for example, populating the debug webpage Catalog view). This user has
 * all privileges on all objects in the server.
 */
public class ImpalaInternalAdminUser extends User {
  private final static ImpalaInternalAdminUser instance_ = new ImpalaInternalAdminUser();

  private ImpalaInternalAdminUser() {
    super("Impala Internal Admin User");
  }

  /*
   * Returns an instance of the ImpalaInternalAdminUser.
   */
  public static ImpalaInternalAdminUser getInstance() { return instance_; }
}
