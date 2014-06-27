package com.skplanet.querycache.server.auth;

import com.google.common.base.Preconditions;

/*
 * Represents a privilege request in the context of an Authorizeable object. If no
 * Authorizeable object is provided, it represents a privilege request on the server.
 * For example, SELECT on table Foo in database Bar.
 */
public class PrivilegeRequest {
  private final Authorizeable authorizeable_;
  private final Privilege privilege_;

  public PrivilegeRequest(Authorizeable authorizeable, Privilege privilege) {
    Preconditions.checkNotNull(authorizeable);
    Preconditions.checkNotNull(privilege);
    authorizeable_ = authorizeable;
    privilege_ = privilege;
  }

  public PrivilegeRequest(Privilege privilege) {
    Preconditions.checkNotNull(privilege);
    authorizeable_ = null;
    privilege_ = privilege;
  }

  /*
   * Name of the Authorizeable. Authorizeable refers to the server if it's null.
   */
  public String getName() {
    return (authorizeable_ != null) ? authorizeable_.getName() : "server";
  }

  /*
   * Requested privilege on the Authorizeable.
   */
  public Privilege getPrivilege() { return privilege_; }


  /*
   * Returns Authorizeable object. Null if the request is for server-level permission.
   */
  public Authorizeable getAuthorizeable() { return authorizeable_; }
}
