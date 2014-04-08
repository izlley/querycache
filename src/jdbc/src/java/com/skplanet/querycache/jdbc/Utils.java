package com.skplanet.querycache.jdbc;

import java.net.URI;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.skplanet.querycache.cli.thrift.TStatus;
import com.skplanet.querycache.cli.thrift.TStatusCode;

public class Utils {
  /**
    * The required prefix for the connection URL.
    */
  public static final String URL_PREFIX = ".+:.+://"; 
  //"jdbc:bdb://", "jdbc:impala://", "thrift:impala://", "cli:hbase://", "jdbc:hive://, ..."

  /**
    * If host is provided, without a port.
    */
  public static final String DEFAULT_PORT = "8282";

  /**
   * QC's default database name
   */
  public static final String DEFAULT_DATABASE = "default";

  private static final String URI_JDBC_PREFIX = "jdbc:";

  public static class JdbcConnectionParams {
    private String host = null;
    private int port;
    //String
    private String service = null;
    private String protocol = null;
    private String dbName = DEFAULT_DATABASE;
    private Map<String,String> qcConfs = new HashMap<String,String>();
    private Map<String,String> qcVars = new HashMap<String,String>();
    private Map<String,String> sessionVars = new HashMap<String,String>();
    private boolean isEmbeddedMode = false;

    public JdbcConnectionParams() {
    }

    public String getHost() {
      return host;
    }
    public String getService() {
      return service;
    }
    public String getProtocol() {
      return protocol;
    }
    public int getPort() {
      return port;
    }
    public String getDbName() {
      return dbName;
    }
    public Map<String, String> getQCConfs() {
      return qcConfs;
    }
    public Map<String,String> getQCVars() {
      return qcVars;
    }
    public boolean isEmbeddedMode() {
      return isEmbeddedMode;
    }
    public Map<String, String> getSessionVars() {
      return sessionVars;
    }

    public void setHost(String host) {
      this.host = host;
    }
    public void setService(String service) {
      this.service = service;
    }
    public void setProtocol(String protocol) {
      this.protocol = protocol;
    }
    public void setPort(int port) {
      this.port = port;
    }
    public void setDbName(String dbName) {
      this.dbName = dbName;
    }
    public void setQCConfs(Map<String, String> qcConfs) {
      this.qcConfs = qcConfs;
    }
    public void setQCVars(Map<String,String> qcVars) {
      this.qcVars = qcVars;
    }
    public void setEmbeddedMode(boolean embeddedMode) {
      this.isEmbeddedMode = embeddedMode;
    }
    public void setSessionVars(Map<String, String> sessionVars) {
      this.sessionVars = sessionVars;
    }
  }


  /**
   * Convert QC types to sql types.
   * @param type
   * @return Integer java.sql.Types values
   * @throws SQLException
   */
  public static int qcTypeToSqlType(String type) throws SQLException {
    if ("string".equalsIgnoreCase(type)) {
      return Types.VARCHAR;
    } else if ("char".equalsIgnoreCase(type)) {
      return Types.CHAR;
    } else if ("float".equalsIgnoreCase(type)) {
      return Types.FLOAT;
    } else if ("double".equalsIgnoreCase(type)) {
      return Types.DOUBLE;
    } else if ("boolean".equalsIgnoreCase(type)) {
      return Types.BOOLEAN;
    } else if ("tinyint".equalsIgnoreCase(type)) {
      return Types.TINYINT;
    } else if ("smallint".equalsIgnoreCase(type)) {
      return Types.SMALLINT;
    } else if ("int".equalsIgnoreCase(type)) {
      return Types.INTEGER;
    } else if ("bigint".equalsIgnoreCase(type)) {
      return Types.BIGINT;
    } else if ("timestamp".equalsIgnoreCase(type)) {
      return Types.TIMESTAMP;
    } else if ("decimal".equalsIgnoreCase(type)) {
      return Types.DECIMAL;
    } else if ("binary".equalsIgnoreCase(type)) {
      return Types.BINARY;
    } else if (type.startsWith("map<")) {
      return Types.VARCHAR;
    } else if (type.startsWith("array<")) {
      return Types.VARCHAR;
    } else if (type.startsWith("struct<")) {
      return Types.VARCHAR;
    }
    throw new SQLException("Unrecognized column type: " + type);
  }

  // Verify success or success_with_info status, else throw SQLException
  public static void verifySuccessWithInfo(TStatus status) throws SQLException {
    verifySuccess(status, true);
  }

  // Verify success status, else throw SQLException
  public static void verifySuccess(TStatus status) throws SQLException {
    verifySuccess(status, false);
  }

  // Verify success and optionally with_info status, else throw SQLException
  public static void verifySuccess(TStatus status, boolean withInfo) throws SQLException {
    if ((status.getStatusCode() != TStatusCode.SUCCESS_STATUS) &&
        (!withInfo || (status.getStatusCode() != TStatusCode.SUCCESS_WITH_INFO_STATUS))) {
      throw new SQLException(status.getErrorMessage(),
           status.getSqlState(), status.getErrorCode());
      }
  }

  /**
   * Parse JDBC connection URL
   * The format of the URL is jdbc:<servicename>://<host>:<port>/dbName;sess_var_list?qc_conf_list#qc_var_list
   * where the optional sess, conf and var lists are semicolon separated <key>=<val> pairs.
   * examples -
   *  jdbc:bdb://ubuntu:11000/db2?qc.cli.conf.printheader=true;qc.exec.mode.local.auto.inputbytes.max=9999#stab=salesTable;icol=customerID
   *  jdbc:impala://ubuntu:11000/db2;user=foo;password=bar
   *
   * Note that currently the session properties are not used.
   *
   * @param uri
   * @return
   */
  public static JdbcConnectionParams parseURL(String uri) throws IllegalArgumentException {
    JdbcConnectionParams connParams = new JdbcConnectionParams();

    /*
    boolean isMatch = false;
    int i = 0;
    for (; i < URL_PREFIX.length; i++) {
      if (Pattern.matches(URL_PREFIX[i] + ".*", uri)) {
        isMatch = true;
        break;
      }
    }
    */
    
    if (!Pattern.matches(URL_PREFIX + ".*", uri)) {
      throw new IllegalArgumentException("Bad URL format: Missing prefix " + URL_PREFIX);
    }

    //URI jdbcURI = URI.create(uri.substring(URI_JDBC_PREFIX.length()));
    int indCol = uri.indexOf(':');
    int indCol2st = uri.indexOf(':', indCol + 1);
    String protocol = uri.substring(0,indCol);
    connParams.setProtocol(protocol);
    String service = uri.substring(indCol + 1, indCol2st);
    connParams.setService(service);
    URI jdbcURI = URI.create(uri.substring(indCol + 1));

    if((jdbcURI.getAuthority() != null) && (jdbcURI.getHost()==null)){
       throw new IllegalArgumentException("Bad URL format. Hostname not found "
           + " in authority part of the url: " + jdbcURI.getAuthority()
           + ". Are you missing a '/' after the hostname ?");
    }

    connParams.setHost(jdbcURI.getHost());
    if (connParams.getHost() == null) {
      connParams.setEmbeddedMode(true);
    } else {
      int port = jdbcURI.getPort();
      if (port == -1) {
        port = Integer.valueOf(DEFAULT_PORT);
      }
      connParams.setPort(port);
    }

    // key=value pattern
    Pattern pattern = Pattern.compile("([^;]*)=([^;]*)[;]?");

    // dbname and session settings
    String sessVars = jdbcURI.getPath();
    if ((sessVars == null) || sessVars.isEmpty()) {
      connParams.setDbName(DEFAULT_DATABASE);
    } else {
      // removing leading '/' returned by getPath()
      sessVars = sessVars.substring(1);
      if (!sessVars.contains(";")) {
        // only dbname is provided
        connParams.setDbName(sessVars);
      } else {
        // we have dbname followed by session parameters
        connParams.setDbName(sessVars.substring(0, sessVars.indexOf(';')));
        sessVars = sessVars.substring(sessVars.indexOf(';')+1);
        if (sessVars != null) {
          Matcher sessMatcher = pattern.matcher(sessVars);
          while (sessMatcher.find()) {
            if (connParams.getSessionVars().put(sessMatcher.group(1), sessMatcher.group(2)) != null) {
              throw new IllegalArgumentException("Bad URL format: Multiple values for property " + sessMatcher.group(1));
            }
          }
        }
      }
    }

    // parse qc conf settings
    String confStr = jdbcURI.getQuery();
    if (confStr != null) {
      Matcher confMatcher = pattern.matcher(confStr);
      while (confMatcher.find()) {
        connParams.getQCConfs().put(confMatcher.group(1), confMatcher.group(2));
      }
    }

    // parse qc var settings
    String varStr = jdbcURI.getFragment();
    if (varStr != null) {
      Matcher varMatcher = pattern.matcher(varStr);
      while (varMatcher.find()) {
        connParams.getQCVars().put(varMatcher.group(1), varMatcher.group(2));
      }
    }

    return connParams;
  }

  public static SQLWarning addWarning(SQLWarning warningChain, SQLWarning newWarning) {
    if (warningChain == null) {
      return newWarning;
    } else {
      warningChain.setNextWarning(newWarning);
      return warningChain;
    }
  }


}
