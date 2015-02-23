package com.skplanet.querycache.server.auth;

/*
 * Class that represents a User of an Impala session.
 */
public class User {
  private final String name_;

  public User(String name) {
    this.name_ = name;
  }

  public String getName() { return name_; }

  /*
   * Get the short version of the user name (the user's name up to the first '/' or '@')
   * from the full principal name.
   * Similar to: org.apache.hadoop.security.UserGroupInformation.getShortName()
   */
  public String getShortName() {
    int idx = name_.indexOf('/');
    int idx2 = name_.indexOf('@');
    int endIdx = Math.min(idx, idx2);
    // At least one of them was not found.
    if (endIdx == -1) endIdx = Math.max(idx,  idx2);

    // If neither are found (or are found at the beginning of the user name),
    // return the username.
    return (endIdx <= 0) ? name_ : name_.substring(0, endIdx);
  }
}
