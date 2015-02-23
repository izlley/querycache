package com.skplanet.querycache.server.auth;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.sentry.core.common.Authorizable;
import org.apache.sentry.policy.db.SimpleDBPolicyEngine;
import org.apache.sentry.provider.file.ResourceAuthorizationProvider;
import org.apache.sentry.provider.file.SimpleFileProviderBackend;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/*
 * Class used to check whether a user has access to a given resource.
 */
public class AuthorizationChecker {
  private final ResourceAuthorizationProvider provider_;
  private final AuthorizationConfig config_;

  /*
   * Creates a new AuthorizationChecker based on the config values.
   */
  public AuthorizationChecker(AuthorizationConfig config) {
    Preconditions.checkNotNull(config);
    config_ = config;
    if (config.isEnabled()) {
      provider_ = createAuthorizationProvider(config);
      Preconditions.checkNotNull(provider_);
    } else {
      provider_ = null;
    }
  }

  /*
   * Creates a new ResourceAuthorizationProvider based on the given configuration.
   */
  private static ResourceAuthorizationProvider
      createAuthorizationProvider(AuthorizationConfig config) {
    try {
      // Try to create an instance of the specified policy provider class.
      // Re-throw any exceptions that are encountered.
      return (ResourceAuthorizationProvider) ConstructorUtils.invokeConstructor(
        Class.forName(config.getPolicyProviderClassName()),
        new Object[] {config.getPolicyFile(), new SimpleDBPolicyEngine(
          config.getServerName(), new SimpleFileProviderBackend(config.getPolicyFile()))}
        );
    } catch (Exception e) {
      // Re-throw as unchecked exception.
      throw new IllegalStateException(
          "Error creating ResourceAuthorizationProvider: " + e.getMessage(), e);
    }
  }

  /*
   * Returns the configuration used to create this AuthorizationProvider.
   */
  public AuthorizationConfig getConfig() {
    return config_;
  }

  /*
   * Returns true if the given user has permission to execute the given
   * request, false otherwise. Always returns true if authorization is disabled.
   */
  public boolean hasAccess(String user, PrivilegeRequest request) {
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(request);

    // If authorization is not enabled the user will always have access. If this is
    // an internal request, the user will always have permission.
    if (!config_.isEnabled()) {
      return true;
    }

    EnumSet<org.apache.sentry.core.model.db.DBModelAction> actions =
        request.getPrivilege().getHiveActions();

    List<Authorizable> authorizeables = Lists.newArrayList();
    authorizeables.add(new org.apache.sentry.core.model.db.Server(config_.getServerName()));
    // If request.getAuthorizeable() is null, the request is for server-level permission.
    if (request.getAuthorizeable() != null) {
      authorizeables.addAll(request.getAuthorizeable().getHiveAuthorizeableHierarchy());
    }

    // The Hive Access API does not currently provide a way to check if the user
    // has any privileges on a given resource.
    if (request.getPrivilege().getAnyOf()) {
      for (org.apache.sentry.core.model.db.DBModelAction action: actions) {
        if (provider_.hasAccess(new org.apache.sentry.core.common.Subject(user),
            authorizeables, EnumSet.of(action))) {
          return true;
        }
      }
      return false;
    }
    return provider_.hasAccess(new org.apache.sentry.core.common.Subject(user),
        authorizeables, actions);
  }
}