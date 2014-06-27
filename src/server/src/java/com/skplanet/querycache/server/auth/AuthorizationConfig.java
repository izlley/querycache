package com.skplanet.querycache.server.auth;

import org.apache.sentry.provider.file.ResourceAuthorizationProvider;

import com.google.common.base.Preconditions;

/*
 * Class that contains configuration details for Impala authorization.
 */
public class AuthorizationConfig {
  private final String serverName_;
  private final String policyFile_;
  private final String udfWLFile_;
  private final String policyProviderClassName_;

  public AuthorizationConfig(String serverName, String policyFile,
      String udfListFile, String policyProviderClassName) {
    serverName_ = serverName;
    policyFile_ = policyFile;
    udfWLFile_  = udfListFile;
    policyProviderClassName_ = policyProviderClassName;
  }

  /*
   * Validates the authorization configuration and throws an AuthorizationException
   * if any problems are found. If authorization is disabled, config checks are skipped.
   */
  public void validateConfig() throws IllegalArgumentException {
    // If authorization is not enabled, config checks are skipped.
    if (!isEnabled()) {
      return;
    }

    if (serverName_ == null || serverName_.isEmpty()) {
      throw new IllegalArgumentException("Authorization is enabled but the server name" +
          " is null or empty. Set the server name using the impalad --server_name flag."
          );
    }
    if (policyFile_ == null || policyFile_.isEmpty()) {
      throw new IllegalArgumentException("Authorization is enabled but the policy file" +
          " path was null or empty. Set the policy file using the " +
          "--authorization_policy_file impalad flag.");
    }
    if (policyProviderClassName_ == null || policyProviderClassName_.isEmpty()) {
      throw new IllegalArgumentException("Authorization is enabled but the " +
          "authorization policy provider class name is null or empty. Set the class " +
          "name using the --authorization_policy_provider_class impalad flag.");
    }
    Class<?> providerClass = null;
    try {
      // Get the Class object without performing any initialization.
      providerClass = Class.forName(policyProviderClassName_, false,
          this.getClass().getClassLoader());
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(String.format("The authorization policy " +
          "provider class '%s' was not found.", policyProviderClassName_), e);
    }
    Preconditions.checkNotNull(providerClass);
    if (!ResourceAuthorizationProvider.class.isAssignableFrom(providerClass)) {
      throw new IllegalArgumentException(String.format("The authorization policy " +
          "provider class '%s' must be a subclass of '%s'.",
          policyProviderClassName_,
          ResourceAuthorizationProvider.class.getName()));
    }
  }

  /*
   * Returns true if authorization is enabled.
   * If either serverName or policyFile is set (not null or empty), authorization
   * is considered enabled.
   */
  public boolean isEnabled() {
    return (serverName_ != null && !serverName_.isEmpty()) ||
           (policyFile_ != null && !policyFile_.isEmpty());
  }

  /*
   * Returns an AuthorizationConfig object that has authorization disabled.
   */
  public static AuthorizationConfig createAuthDisabledConfig() {
    return new AuthorizationConfig(null, null, null, null);
  }

  /*
   * The server name to secure.
   */
  public String getServerName() { return serverName_; }

  /*
   * The policy file path.
   */
  public String getPolicyFile() { return policyFile_; }

  /*
   * The UDF whitelist file path.
   */
  public String getUdfWLFile() { return udfWLFile_; }
  /*
   * The full class name of the authorization policy provider. For example:
   * org.apache.sentry.provider.file.HadoopGroupResourceAuthorizationProvider.
   */
  public String getPolicyProviderClassName() { return policyProviderClassName_; }
}
