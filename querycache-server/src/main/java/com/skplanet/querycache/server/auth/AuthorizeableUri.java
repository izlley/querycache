package com.skplanet.querycache.server.auth;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/*
 * Class used to authorize access to a URI.
 */
public class AuthorizeableUri implements Authorizeable {
  private final String uriName_;

  public AuthorizeableUri(String uriName) {
    Preconditions.checkNotNull(uriName);
    uriName_ = uriName;
  }

  @Override
  public List<org.apache.sentry.core.common.Authorizable> getHiveAuthorizeableHierarchy() {
    org.apache.sentry.core.model.db.AccessURI accessURI =
        new org.apache.sentry.core.model.db.AccessURI(uriName_);
    return Lists.newArrayList((org.apache.sentry.core.common.Authorizable) accessURI);
  }

  @Override
  public String getName() { return uriName_; }
}
