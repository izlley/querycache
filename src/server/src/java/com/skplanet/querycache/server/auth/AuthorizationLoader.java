package com.skplanet.querycache.server.auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.skplanet.querycache.server.QCConfigKeys;
import com.skplanet.querycache.server.QueryCacheServer;
import com.skplanet.querycache.server.sqlcompiler.AuthorizationException;

public class AuthorizationLoader {
  private static final Logger LOG = LoggerFactory.getLogger(AuthorizationLoader.class);
  
  private final AuthorizationConfig authConfig;
  private AuthorizationChecker authChecker = null;
  private List<String> udfWhiteList = new ArrayList<String>();
  
  private final ScheduledExecutorService policyReader = 
    Executors.newScheduledThreadPool(1);
  private static final long refreshInverval = QueryCacheServer.conf.getLong(
    QCConfigKeys.QC_AUTHORIZATION_POLICY_RELOAD_CYCLE_MILLI,
    QCConfigKeys.QC_AUTHORIZATION_POLICY_RELOAD_CYCLE_MILLI_DEFAULT);
  // Lock used to sync refreshing the AuthorizationChecker.
  private final ReentrantReadWriteLock authCheckerLock = new ReentrantReadWriteLock();
  
  public AuthorizationLoader(AuthorizationConfig authConfig) {
    this.authConfig = authConfig;
    authChecker = new AuthorizationChecker(authConfig);
    if (authConfig.getUdfWLFile() != null) {
      reloadUdfWhiteList(authConfig.getUdfWLFile());
    }
    if (authConfig.isEnabled()) {
      policyReader.scheduleAtFixedRate(
        new AuthorizationPolicyReader(authConfig),
        refreshInverval, refreshInverval, TimeUnit.MILLISECONDS);
    }
  }
  
  private class AuthorizationPolicyReader implements Runnable {
    private final AuthorizationConfig config;

    public AuthorizationPolicyReader(AuthorizationConfig config) {
      this.config = config;
    }

    public void run() {
      LOG.info("Reloading authorization policy file from: " + config.getPolicyFile() +
        ", UDF whitelist file from: " + config.getUdfWLFile());
      authCheckerLock.writeLock().lock();
      try {
        authChecker = new AuthorizationChecker(config);
        // reload udf whitelist
        if (authConfig.getUdfWLFile() != null) {
          reloadUdfWhiteList(config.getUdfWLFile());
        }
      } finally {
        authCheckerLock.writeLock().unlock();
      }
    }
  }
  
  /**
   * Checks whether a given user has sufficient privileges to access an authorizeable
   * object.
   * @throws AuthorizationException - If the user does not have sufficient privileges.
   */
  public void checkAccess(String user, PrivilegeRequest privilegeRequest)
      throws AuthorizationException {
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(privilegeRequest);

    if (!hasAccess(user, privilegeRequest)) {
      Privilege privilege = privilegeRequest.getPrivilege();
      if (EnumSet.of(Privilege.ANY, Privilege.ALL, Privilege.VIEW_METADATA)
          .contains(privilege)) {
        throw new AuthorizationException(String.format(
            "User '%s' does not have privileges to access: %s",
            user, privilegeRequest.getName()));
      } else {
        throw new AuthorizationException(String.format(
            "User '%s' does not have privileges to execute '%s' on: %s",
            user, privilege, privilegeRequest.getName()));
      }
    }
  }
  
  public void checkCreateDropFunctionAccess(String user) throws AuthorizationException {
    Preconditions.checkNotNull(user);
    if (!hasAccess(user, new PrivilegeRequest(Privilege.ALL))) {
      throw new AuthorizationException(String.format(
          "User '%s' does not have privileges to CREATE/DROP functions.",
          user));
    }
  }

  /**
   * Checks whether the given User has permission to perform the given request.
   * Returns true if the User has privileges, false if the User does not.
   */
  private boolean hasAccess(String user, PrivilegeRequest request) {
    authCheckerLock.readLock().lock();
    try {
      Preconditions.checkNotNull(authChecker);
      return authChecker.hasAccess(user, request);
    } finally {
      authCheckerLock.readLock().unlock();
    }
  }
  
  private void reloadUdfWhiteList(String udfFilePath) {
    try {
      Scanner s = new Scanner(new File(udfFilePath));
      while (s.hasNext()) {
        udfWhiteList.add((String)(s.next()).toUpperCase());
      }
      s.close();
      LOG.info("Load UDF whilelist files: Opening " + udfFilePath);
      } catch (FileNotFoundException e) {
        LOG.warn("Roading the UDF whilelist file is failed: path=" + udfFilePath);
    }
  }
  
  public boolean isUDFExist(String udf) {
    return (Collections.binarySearch(udfWhiteList, udf.toUpperCase()) >= 0);
  }
}
