package com.skplanet.querycache.jdbc;

import java.io.IOException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import org.apache.thrift.transport.TSocket;
import com.skplanet.querycache.cli.thrift.TCLIService;
import com.skplanet.querycache.cli.thrift.TCloseSessionReq;
import com.skplanet.querycache.cli.thrift.TOpenSessionReq;
import com.skplanet.querycache.cli.thrift.TOpenSessionResp;
import com.skplanet.querycache.cli.thrift.TProtocolVersion;
import com.skplanet.querycache.cli.thrift.TSessionHandle;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * QCConnection.
 * 
 */
public class QCConnection implements java.sql.Connection {
  private static final String QC_AUTH_TYPE = "auth";
  private static final String QC_AUTH_QOP = "sasl.qop";
  private static final String QC_AUTH_SIMPLE = "noSasl";
  private static final String QC_AUTH_TOKEN = "delegationToken";
  private static final String QC_AUTH_USER = "user";
  private static final String QC_AUTH_PRINCIPAL = "principal";
  private static final String QC_AUTH_PASSWD = "password";
  private static final String QC_ANONYMOUS_USER = "anonymous";
  private static final String QC_ANONYMOUS_PASSWD = "anonymous";
  private static final String QC_USE_SSL = "ssl";
  private static final String QC_SSL_TRUST_STORE = "sslTrustStore";
  private static final String QC_SSL_TRUST_STORE_PASSWORD = "trustStorePassword";
  private static final String QC_SO_TIMEOUT = "socketTimeout";

  private TTransport transport;
  private TCLIService.Iface client;
  private boolean isClosed = true;
  private SQLWarning warningChain = null;
  private TSessionHandle sessHandle = null;
  private final List<TProtocolVersion> supportedProtocols = new LinkedList<TProtocolVersion>();
  private int loginTimeout = 0;
  private int socketTimeout = 0;

  /**
   * TODO: - parse uri (use java.net.URI?).
   */
  public QCConnection(String uri, Properties info) throws SQLException {
    Utils.JdbcConnectionParams connParams;
    setupLoginTimeout();
    try {
      connParams = Utils.parseURL(uri);
    } catch (IllegalArgumentException e) {
      throw new SQLException(e);
    }

    // extract user/password from JDBC connection properties if its not
    // supplied in the connection URL
    if (info.containsKey(QC_AUTH_USER)) {
      connParams.getSessionVars().put(QC_AUTH_USER,
          info.getProperty(QC_AUTH_USER));
      if (info.containsKey(QC_AUTH_PASSWD)) {
        connParams.getSessionVars().put(QC_AUTH_PASSWD,
            info.getProperty(QC_AUTH_PASSWD));
       }
    }
    if (info.containsKey(QC_AUTH_TYPE)) {
      connParams.getSessionVars().put(QC_AUTH_TYPE,
          info.getProperty(QC_AUTH_TYPE));
    }
    if (connParams.getQCConfs().containsKey(QC_SO_TIMEOUT)) {
      socketTimeout = Integer.parseInt(connParams.getQCConfs().get(QC_SO_TIMEOUT));
      connParams.getQCConfs().remove(QC_SO_TIMEOUT);
    }

    openTransport(uri, connParams.getHost(), connParams.getPort(),
        connParams.getSessionVars());

    // currently only V1 is supported
    supportedProtocols.add(TProtocolVersion.QUERYCACHE_CLI_PROTOCOL_V1);

    // open client session
    openSession(uri, connParams);

    configureConnection(connParams);
  }

  private void configureConnection(Utils.JdbcConnectionParams connParams)
      throws SQLException {
    // for remote JDBC client, try to set the conf var using 'set foo=bar'
    Statement stmt = createStatement();
    for (Entry<String, String> qcConf : connParams.getQCConfs()
        .entrySet()) {
      stmt.execute("set " + qcConf.getKey() + "=" + qcConf.getValue());
      stmt.close();
    }
  }

  private void openTransport(String uri, String host, int port,
      Map<String, String> sessConf) throws SQLException {
    // With this socketTimeout option set to a non-zero timeout, a read() call on
    // the InputStream associated with this Socket will block for only this amount of time.
    // If the timeout expires, a java.net.SocketTimeoutException is raised,
    // though the Socket is still valid. The timeout must be > 0. 
    // A timeout of zero is interpreted as an infinite timeout.
    transport = new TSocket(host, port, socketTimeout);

    // TProtocol protocol = new TBinaryProtocol(transport);
    TProtocol protocol = new TCompactProtocol(transport);
    client = new TCLIService.Client(protocol);
    
    try {
      transport.open();
    } catch (TTransportException e) {
      throw new SQLException("Could not establish connection to " + uri + ": "
          + e.getMessage(), " 08S01", e);
    }
  }

  private void openSession(String uri, Utils.JdbcConnectionParams connParams)
      throws SQLException {
    Map<String, String> sessVars = connParams.getSessionVars();
    TOpenSessionReq openReq = new TOpenSessionReq();

    try {
      openReq.url = connParams.getProtocol() + ":" + connParams.getService();
      openReq.username = connParams.getSessionVars().get("user");
      openReq.password = connParams.getSessionVars().get("password");
      TOpenSessionResp openResp = client.OpenSession(openReq);
      // validate connection
      Utils.verifySuccess(openResp.getStatus());
      if (!supportedProtocols.contains(openResp.getServerProtocol())) { // getServerProtocolVersion()))
                                                                        // {
        throw new TException("Unsupported QC protocol");
      }
      sessHandle = openResp.getSessionHandle();
    } catch (TException e) {
      throw new SQLException("Could not establish connection to " + uri + ": "
          + e.getMessage(), " 08S01", e);
    }
    isClosed = false;
  }

  // copy loginTimeout from driver manager. Thrift timeout needs to be in millis
  private void setupLoginTimeout() {
    long timeOut = TimeUnit.SECONDS.toMillis(DriverManager.getLoginTimeout());
    if (timeOut > Integer.MAX_VALUE) {
      loginTimeout = Integer.MAX_VALUE;
    } else {
      loginTimeout = (int) timeOut;
    }
  }

  public void abort(Executor executor) throws SQLException {
    // JDK 1.7
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#clearWarnings()
   */

  public void clearWarnings() throws SQLException {
    warningChain = null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#close()
   */

  public void close() throws SQLException {
    if (!isClosed) {
      TCloseSessionReq closeReq = new TCloseSessionReq(sessHandle);
      try {
        client.CloseSession(closeReq);
      } catch (TException e) {
        throw new SQLException("Error while cleaning up the server resources",
            e);
      } finally {
        isClosed = true;
        if (transport != null) {
          transport.close();
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#commit()
   */

  public void commit() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createArrayOf(java.lang.String,
   * java.lang.Object[])
   */

  public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createBlob()
   */

  public Blob createBlob() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createClob()
   */

  public Clob createClob() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createNClob()
   */

  public NClob createNClob() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createSQLXML()
   */

  public SQLXML createSQLXML() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /**
   * Creates a Statement object for sending SQL statements to the database.
   * 
   * @throws SQLException
   *           if a database access error occurs.
   * @see java.sql.Connection#createStatement()
   */

  public Statement createStatement() throws SQLException {
    if (isClosed) {
      throw new SQLException("Can't create Statement, connection is closed");
    }
    return new QCStatement(client, sessHandle);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createStatement(int, int)
   */

  public Statement createStatement(int resultSetType, int resultSetConcurrency)
      throws SQLException {
    if (resultSetConcurrency != ResultSet.CONCUR_READ_ONLY) {
      throw new SQLException("Method not supported");
    }
    if (resultSetType == ResultSet.TYPE_SCROLL_SENSITIVE) {
      throw new SQLException("Method not supported");
    }
    return new QCStatement(client, sessHandle,
        resultSetType == ResultSet.TYPE_SCROLL_INSENSITIVE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createStatement(int, int, int)
   */

  public Statement createStatement(int resultSetType, int resultSetConcurrency,
      int resultSetHoldability) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
   */

  public Struct createStruct(String typeName, Object[] attributes)
      throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getAutoCommit()
   */

  public boolean getAutoCommit() throws SQLException {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getCatalog()
   */

  public String getCatalog() throws SQLException {
    return "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getClientInfo()
   */

  public Properties getClientInfo() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getClientInfo(java.lang.String)
   */

  public String getClientInfo(String name) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getHoldability()
   */

  public int getHoldability() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getMetaData()
   */

  public DatabaseMetaData getMetaData() throws SQLException {
    return new QCDatabaseMetaData(client, sessHandle);
  }

  public int getNetworkTimeout() throws SQLException {
    // JDK 1.7
    throw new SQLException("Method not supported");
  }

  public String getSchema() throws SQLException {
    // JDK 1.7
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getTransactionIsolation()
   */

  public int getTransactionIsolation() throws SQLException {
    return Connection.TRANSACTION_NONE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getTypeMap()
   */

  public Map<String, Class<?>> getTypeMap() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getWarnings()
   */

  public SQLWarning getWarnings() throws SQLException {
    return warningChain;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#isClosed()
   */

  public boolean isClosed() throws SQLException {
    return isClosed;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#isReadOnly()
   */

  public boolean isReadOnly() throws SQLException {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#isValid(int)
   */

  public boolean isValid(int timeout) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#nativeSQL(java.lang.String)
   */

  public String nativeSQL(String sql) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareCall(java.lang.String)
   */

  public CallableStatement prepareCall(String sql) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
   */

  public CallableStatement prepareCall(String sql, int resultSetType,
      int resultSetConcurrency) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
   */

  public CallableStatement prepareCall(String sql, int resultSetType,
      int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String)
   */

  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return new QCPreparedStatement(client, sessHandle, sql);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int)
   */

  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
      throws SQLException {
    return new QCPreparedStatement(client, sessHandle, sql);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
   */

  public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
      throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String,
   * java.lang.String[])
   */

  public PreparedStatement prepareStatement(String sql, String[] columnNames)
      throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
   */

  public PreparedStatement prepareStatement(String sql, int resultSetType,
      int resultSetConcurrency) throws SQLException {
    return new QCPreparedStatement(client, sessHandle, sql);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
   */

  public PreparedStatement prepareStatement(String sql, int resultSetType,
      int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
   */

  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#rollback()
   */

  public void rollback() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#rollback(java.sql.Savepoint)
   */

  public void rollback(Savepoint savepoint) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setAutoCommit(boolean)
   */

  public void setAutoCommit(boolean autoCommit) throws SQLException {
    if (autoCommit) {
      throw new SQLException("enabling autocommit is not supported");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setCatalog(java.lang.String)
   */

  public void setCatalog(String catalog) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setClientInfo(java.util.Properties)
   */

  public void setClientInfo(Properties properties)
      throws SQLClientInfoException {
    // TODO Auto-generated method stub
    throw new SQLClientInfoException("Method not supported", null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)
   */

  public void setClientInfo(String name, String value)
      throws SQLClientInfoException {
    // TODO Auto-generated method stub
    throw new SQLClientInfoException("Method not supported", null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setHoldability(int)
   */

  public void setHoldability(int holdability) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  public void setNetworkTimeout(Executor executor, int milliseconds)
      throws SQLException {
    // JDK 1.7
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setReadOnly(boolean)
   */

  public void setReadOnly(boolean readOnly) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setSavepoint()
   */

  public Savepoint setSavepoint() throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setSavepoint(java.lang.String)
   */

  public Savepoint setSavepoint(String name) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  public void setSchema(String schema) throws SQLException {
    // JDK 1.7
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setTransactionIsolation(int)
   */

  public void setTransactionIsolation(int level) throws SQLException {
    // TODO: throw an exception?
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setTypeMap(java.util.Map)
   */

  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
   */

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Wrapper#unwrap(java.lang.Class)
   */

  public <T> T unwrap(Class<T> iface) throws SQLException {
    // TODO Auto-generated method stub
    throw new SQLException("Method not supported");
  }

}
