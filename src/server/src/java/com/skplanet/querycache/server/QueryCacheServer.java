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

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

// Generated code
import com.skplanet.querycache.cli.thrift.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.common.*;

public class QueryCacheServer {
  private static final Logger LOG = LoggerFactory.getLogger(QueryCacheServer.class);

  public static final QCConfiguration conf = new QCConfiguration();
  
  public static void main(String [] args) {
    try {
      CLIHandler handler = new CLIHandler();
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
    } catch (Exception e) {
      LOG.error("FATAL error : ", e);
      System.exit(1);
    }
  }

  public static void createServer(TCLIService.Processor processor) {
    try {
      boolean tcpKeepAlive = true;
      
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
      sArgs.minWorkerThreads(conf.getInt(QCConfigKeys.QC_WORKERTHREAD_MIN,
        QCConfigKeys.QC_WORKERTHREAD_MIN_DEFAULT));
      sArgs.maxWorkerThreads(conf.getInt(QCConfigKeys.QC_WORKERTHREAD_MAX,
        QCConfigKeys.QC_WORKERTHREAD_MAX_DEFAULT));
      
      TServer server = new TThreadPoolServer(sArgs);
      
      System.out.println("Starting the QueryCache server...");
      LOG.info("Starting the QueryCache server...");
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
