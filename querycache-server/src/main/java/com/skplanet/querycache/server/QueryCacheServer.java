/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.skplanet.querycache.server;

import com.skplanet.querycache.cli.thrift.TCLIService;
import com.skplanet.querycache.servlet.QCWebApiServlet;
import com.skplanet.querycache.servlet.QCWebSocketServlet;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.*;
import org.apache.thrift.transport.*;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

// Generated code

public class QueryCacheServer {
  private static final Logger LOG = LoggerFactory.getLogger(QueryCacheServer.class);
  public static final QCConfiguration conf = new QCConfiguration();
  public static Server webServer = null;

  public static class QCServerContext implements ServerContext {
    private static AtomicLong nextConnection = new AtomicLong(1);
    private static ConcurrentHashMap<Long, QCServerContext> svrCtxMap = new ConcurrentHashMap<>();

    public final long connectionId;
    public final long threadId;
    public final TSocket socket;
    public final String clientIP;
    public final String serverIP;
    public String clientVersion = "unknown";

    private QCServerContext(long threadId, TSocket socket) {
      this.connectionId = nextConnection.getAndIncrement();
      this.threadId = threadId;
      this.socket = socket;
      this.clientIP = socket.getSocket().getInetAddress().getHostAddress();
      this.serverIP = socket.getSocket().getLocalAddress().getHostAddress();
      svrCtxMap.put(threadId, this);
    }

    private String queryId = null;
    private String driverType = null;
    public void setCurrentQuery(String driverType, String queryId) {
      this.driverType = driverType;
      this.queryId = queryId;
    }
    public void clearCurrentQuery() {
      this.driverType = null;
      this.queryId = null;
    }
    private void cancelCurrentQuery() {
      if (driverType != null && queryId != null) {
        CLIHandler.getInstance().internalCancelStatement(queryId, driverType);
      }
      clearCurrentQuery();
    }

    private StatementExecutor asyncExecutor = null;

    public StatementExecutor getAsyncExecutor() {
      return asyncExecutor;
    }

    public void setAsyncExecutor(StatementExecutor asyncExecutor) {
      this.asyncExecutor = asyncExecutor;
    }

    public static QCServerContext newSvrContext(TSocket s) {
      long threadId = Thread.currentThread().getId();
      QCServerContext svrCtx = svrCtxMap.get(threadId);
      if (svrCtx == null) {
        svrCtx = new QCServerContext(threadId, s);
        svrCtxMap.put(threadId, svrCtx);
        LOG.debug("added thrift svrCtx. remaining : " + svrCtxMap.size());
      }
      return svrCtx;
    }

    public static QCServerContext getSvrContext() {
      long threadId = Thread.currentThread().getId();
      QCServerContext svrCtx = svrCtxMap.get(threadId);
      if (svrCtx == null) {
        LOG.info("thrift svrCtx not found for tid " + threadId);
      }
      return svrCtx;
    }

    public static void removeSvrContext() {
      long threadId = Thread.currentThread().getId();
      QCServerContext ctx = svrCtxMap.get(threadId);
      if (ctx != null) {
        ctx.cancelCurrentQuery();
        svrCtxMap.remove(threadId);
      }

      LOG.debug("removed thrift svrCtx. remaining : " + svrCtxMap.size());
    }
  }

  private static class QueryCacheServerEventHandler implements TServerEventHandler {
    public void preServe() {
      // do nothing
    }

    // methods below are called in the same thread context.
    // (thread which work for specific client connection) : see TThreadPoolServer.java
    public ServerContext createContext(TProtocol input, TProtocol output) {
      // TProtocol is using TTransport, TTransport is an abstract of TSocket
      // current implementation uses same TTransport for input and output
      TSocket s = (TSocket)input.getTransport();
      QCServerContext ctx = QCServerContext.newSvrContext(s);
      LOG.debug("new thrift connection #" + ctx.connectionId + " tid " + ctx.threadId);
      return ctx;
    }

    public void deleteContext(ServerContext serverContext, TProtocol input, TProtocol output) {
      QCServerContext ctx = (QCServerContext)serverContext;
      QCServerContext.removeSvrContext();
      LOG.debug("thrift connection closed #" + ctx.connectionId + " tid " + ctx.threadId);
    }

    public void processContext(ServerContext serverContext, TTransport inputTransport, TTransport outputTransport) {
      // do nothing
    }
  }

  public static ThreadPoolExecutor qcServerExecutorService = null;
  private static class QCServerExecutorService extends ThreadPoolExecutor {
    public QCServerExecutorService(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new SynchronousQueue());
    }
  }

  public static String hostname = "";
  public static String version = "none";

  public static void main(String [] args) {
    CLIHandler handler = CLIHandler.getInstance();

    hostname = System.getProperty("HOSTNAME", "noname");
    version = handler.getClass().getPackage().getImplementationVersion();

    try {
      final TCLIService.Processor processor = new TCLIService.Processor(handler);
      
      Runnable sThriftServer = new Runnable() {
        public void run() {
          createServer(processor);
        }
      };
      /*
      Runnable secure = new Runnable() {
        public void run() {
          secure(processor);
        }
      };
      */
      
      // Add shutdown hook.
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          String shutdownMsg = "Shutting down querycache.";
          LOG.info(shutdownMsg);
        }
      });

      new Thread(sThriftServer).start();
      //new Thread(secure).start();

      webServer = runWebInterface();
      webServer.join();
    } catch (Exception e) {
      LOG.error("FATAL error : ", e);
      System.exit(1);
    }
  }

  public static Server runWebInterface() throws Exception {
    // create web service
    LOG.info("Starting web interface...");
    Server server = new Server(8080);

    ServletContextHandler context_api = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context_api.setContextPath("/api");
    context_api.addServlet(new ServletHolder(new QCWebSocketServlet()), "/websocket/*");
    context_api.addServlet(new ServletHolder(new QCWebApiServlet()), "/*");

    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.setDirectoriesListed(true);
    resource_handler.setWelcomeFiles(new String[]{ "index.html" });
    resource_handler.setResourceBase("./www/");

    ContextHandlerCollection contextCollection = new ContextHandlerCollection();
    contextCollection.setHandlers( new Handler[] { context_api } );

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] {contextCollection, resource_handler});
    server.setHandler(handlers);
    server.start();
    LOG.info("Started web interface...");

    return server;
  }

  public static void createServer(TCLIService.Processor processor) {
    try {
      boolean tcpKeepAlive = true;

      qcServerExecutorService = new QCServerExecutorService(
              conf.getInt(QCConfigKeys.QC_WORKERTHREAD_MIN, QCConfigKeys.QC_WORKERTHREAD_MIN_DEFAULT),
              conf.getInt(QCConfigKeys.QC_WORKERTHREAD_MAX, QCConfigKeys.QC_WORKERTHREAD_MAX_DEFAULT),
              60L,
              TimeUnit.SECONDS);

      //TServerTransport serverTransport = tcpKeepAlive ?
      //  new TServerSocketKeepAlive(Configure.gServerPort) : new TServerSocket(Configure.gServerPort);
      TServerTransport serverTransport = tcpKeepAlive ?
        new TServerSocketKeepAlive(conf.getInt(QCConfigKeys.QC_SERVER_PORT, QCConfigKeys.QC_SERVER_PORT_DEFAULT))
        : new TServerSocket(conf.getInt(QCConfigKeys.QC_SERVER_PORT, QCConfigKeys.QC_SERVER_PORT_DEFAULT));
      // Use this for a multi-threaded server
      // Use CompactProtocol
      TThreadPoolServer.Args sArgs = new TThreadPoolServer.Args(serverTransport).
          processor(processor);
      sArgs.inputProtocolFactory(new TCompactProtocol.Factory());
      sArgs.outputProtocolFactory(new TCompactProtocol.Factory());
      sArgs.executorService(qcServerExecutorService);

      TServer server = new TThreadPoolServer(sArgs);
      server.setServerEventHandler(new QueryCacheServerEventHandler());

      String welcome = "Starting QueryCache server (version " + version + ")";
      System.out.println(welcome);
      LOG.info(welcome);
      server.serve();
    } catch (Exception e) {
      LOG.error("FATAL error : ", e);
      System.exit(1);
    }
  }

  public static void secure(TCLIService.Processor processor) {
    try {
      /*
       * Use TSSLTransportParameters to setup the required SSL parameters. In this example
       * we are setting the keystore and the keystore password. Other things like algorithms,
       * cipher suites, client auth etc can be set. 
       */
      TSSLTransportParameters params = new TSSLTransportParameters();
      // The Keystore contains the private key
      params.setKeyStore("/home/leejy/work/thrift-0.9.1/lib/java/test/.keystore", "thrift", null, null);

      /*
       * Use any of the TSSLTransportFactory to get a server transport with the appropriate
       * SSL configuration. You can use the default settings if properties are set in the command line.
       * Ex: -Djavax.net.ssl.keyStore=.keystore and -Djavax.net.ssl.keyStorePassword=thrift
       * 
       * Note: You need not explicitly call open(). The underlying server socket is bound on return
       * from the factory class. 
       */
      TServerTransport serverTransport = TSSLTransportFactory.getServerSocket(2849, 0, null, params);
      //TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

      // Use this for a multi threaded server
      TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).
        processor(processor));

      System.out.println("Starting the secure server...");
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
