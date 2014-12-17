package com.skplanet.querycache.jdbc;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * QCDriver.
 *
 */
public class QCDriver implements Driver {
  static {
    try {
      java.sql.DriverManager.registerDriver(new QCDriver());
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Is this driver JDBC compliant?
   */
  private static final boolean JDBC_COMPLIANT = false;

  /**
   * The required prefix for the connection URL.
   */
  private static final String URL_PREFIX = Utils.URL_PREFIX;

  /**
   * If host is provided, without a port.
   */
  private static final String DEFAULT_PORT = Utils.DEFAULT_PORT;

  /**
   * Property key for the database name.
   */
  private static final String DBNAME_PROPERTY_KEY = "DBNAME";

  /**
   * Property key for the QueryCache Server host.
   */
  private static final String HOST_PROPERTY_KEY = "HOST";

  /**
   * Property key for the QueryCache Server port.
   */
  private static final String PORT_PROPERTY_KEY = "PORT";

  /**
   *
   */
  public QCDriver() {
    // TODO Auto-generated constructor stub
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      security.checkWrite("foobah");
    }
  }

  /**
   * Checks whether a given url is in a valid format.
   * 
   * The current uri format is: 
   * - syntax -
   *  connstr -> protocol_type ':' service_name '://' hostname ':' portnum *(';' kv_property)
   *  protocol_type -> 'jdbc' | 'thrift' | 'cli'
   *  service_name -> 'bdb' | 'impala'
   *  kv_property -> identifier '=' (number|identifier)
   *  hostname -> identifier | ip_addr
   *  portnum -> +[0-9]
   * 
   * TODO: - write a better regex. - decide on uri format
   */

  public boolean acceptsURL(String url) throws SQLException {
    return Pattern.matches(URL_PREFIX + ".*", url);
  }

  public Connection connect(String url, Properties info) throws SQLException {
    return new QCConnection(url, info);
  }

  /**
   * Package scoped access to the Driver's Major Version 
   * @return The Major version number of the driver. -1 if it cannot be determined from the
   * manifest.mf file.
   */
  static int getMajorDriverVersion() {
    int version = -1;
    try {
      String fullVersion = QCDriver.fetchManifestAttribute(
          Attributes.Name.IMPLEMENTATION_VERSION);
      String[] tokens = fullVersion.split("\\."); //$NON-NLS-1$
      
      if(tokens != null && tokens.length > 0 && tokens[0] != null) {
        version = Integer.parseInt(tokens[0]);
      }
    } catch (Exception e) {
      // Possible reasons to end up here:
      // - Unable to read version from manifest.mf
      // - Version string is not in the proper X.x.xxx format
      version = -1;
    }
    return version;
  }
  
  /**
   * Package scoped access to the Driver's Minor Version 
   * @return The Minor version number of the driver. -1 if it cannot be determined from the
   * manifest.mf file.
   */
  static int getMinorDriverVersion() {
    int version = -1;
    try {
      String fullVersion = QCDriver.fetchManifestAttribute(
          Attributes.Name.IMPLEMENTATION_VERSION);
      String[] tokens = fullVersion.split("\\."); //$NON-NLS-1$
      
      if(tokens != null && tokens.length > 1 && tokens[1] != null) {
        version = Integer.parseInt(tokens[1]);
      }
    } catch (Exception e) {
      // Possible reasons to end up here:
      // - Unable to read version from manifest.mf
      // - Version string is not in the proper X.x.xxx format
      version = -1;
    }
    return version;
  }
  
  /**
   * Returns the major version of this driver.
   */
  public int getMajorVersion() {
    return QCDriver.getMajorDriverVersion();
  }

  /**
   * Returns the minor version of this driver.
   */
  public int getMinorVersion() {
    return QCDriver.getMinorDriverVersion();
  }

  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    // JDK 1.7
    throw new SQLFeatureNotSupportedException("Method not supported");
  }

  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    if (info == null) {
      info = new Properties();
    }

    if (url != null) {
      if (Pattern.matches("^" + URL_PREFIX + ".*$", url)) {
        info = parseURL(url, info);
      }
    }

    DriverPropertyInfo hostProp = new DriverPropertyInfo(HOST_PROPERTY_KEY,
        info.getProperty(HOST_PROPERTY_KEY, ""));
    hostProp.required = false;
    hostProp.description = "Hostname of QueryCache Server";

    DriverPropertyInfo portProp = new DriverPropertyInfo(PORT_PROPERTY_KEY,
        info.getProperty(PORT_PROPERTY_KEY, ""));
    portProp.required = false;
    portProp.description = "Port number of QueryCache Server";

    DriverPropertyInfo dbProp = new DriverPropertyInfo(DBNAME_PROPERTY_KEY,
        info.getProperty(DBNAME_PROPERTY_KEY, "default"));
    dbProp.required = false;
    dbProp.description = "Database name";

    DriverPropertyInfo[] dpi = new DriverPropertyInfo[3];

    dpi[0] = hostProp;
    dpi[1] = portProp;
    dpi[2] = dbProp;

    return dpi;
  }

  /**
   * Returns whether the driver is JDBC compliant.
   */

  public boolean jdbcCompliant() {
    return JDBC_COMPLIANT;
  }

  /**
   * Takes a url in the form of [protocol_name]:[server_name]://[hostname]:[port]/[db_name] and
   * parses it. Everything after [protocol_name]:[server_name]:// is optional.
   * 
   * @param url
   * @param index
   * @param defaults
   * @return
   * @throws java.sql.SQLException
   */
  private Properties parseURL(String url, Properties defaults) throws SQLException {
    Properties urlProps = (defaults != null) ? new Properties(defaults)
        : new Properties();

    if (url == null || !Pattern.matches("^" + URL_PREFIX + ".*$", url)) {
      throw new SQLException("Invalid connection url: " + url);
    }

    int prefixLen = url.indexOf("://") + 3;
    if (url.length() <= prefixLen) {
      return urlProps;
    }

    // [hostname]:[port]/[db_name]
    String connectionInfo = url.substring(prefixLen);

    // [hostname]:[port] [db_name]
    String[] hostPortAndDatabase = connectionInfo.split("/", 2);

    // [hostname]:[port]
    if (hostPortAndDatabase[0].length() > 0) {
      String[] hostAndPort = hostPortAndDatabase[0].split(":", 2);
      urlProps.put(HOST_PROPERTY_KEY, hostAndPort[0]);
      if (hostAndPort.length > 1) {
        urlProps.put(PORT_PROPERTY_KEY, hostAndPort[1]);
      } else {
        urlProps.put(PORT_PROPERTY_KEY, DEFAULT_PORT);
      }
    }

    // [db_name]
    if (hostPortAndDatabase.length > 1) {
      urlProps.put(DBNAME_PROPERTY_KEY, hostPortAndDatabase[1]);
    }

    return urlProps;
  }
  
  /**
   * Lazy-load manifest attributes as needed.
   */
  private static Attributes manifestAttributes = null;

  /**
   * Loads the manifest attributes from the jar.
   * 
   * @throws java.net.MalformedURLException
   * @throws IOException
   */
  private static synchronized void loadManifestAttributes() throws IOException {
    if (manifestAttributes != null) {
      return;
    }
    Class<?> clazz = QCDriver.class;
    String classContainer = clazz.getProtectionDomain().getCodeSource()
        .getLocation().toString();
    URL manifestUrl = new URL("jar:" + classContainer
        + "!/META-INF/MANIFEST.MF");
    Manifest manifest = new Manifest(manifestUrl.openStream());
    manifestAttributes = manifest.getMainAttributes();
  }

  /**
   * Package scoped to allow manifest fetching from other QCDriver classes
   * Helper to initialize attributes and return one.
   * 
   * @param attributeName
   * @return
   * @throws SQLException
   */
  static String fetchManifestAttribute(Attributes.Name attributeName)
      throws SQLException {
    try {
      loadManifestAttributes();
    } catch (IOException e) {
      throw new SQLException("Couldn't load manifest attributes.", e);
    }
    return manifestAttributes.getValue(attributeName);
  }

}