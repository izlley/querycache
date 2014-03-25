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
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

// Generated code
import com.skplanet.querycache.thrift.*;
import com.skplanet.querycache.cli.thrift.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skplanet.querycache.server.common.*;

import java.util.HashMap;

public class QueryCacheServer {
  private static final Logger LOG = LoggerFactory.getLogger(QueryCacheServer.class);

  public static CLIHandler handler;

  public static TCLIService.Processor processor;

  public static void main(String [] args) {
    try {
      handler = new CLIHandler();
      processor = new TCLIService.Processor(handler);
      //processor.process(arg0, arg1);
      
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

      new Thread(sThriftServer).start();
      //new Thread(secure).start();
    } catch (Exception e) {
      LOG.error("FATAL error : ", e);
    }
  }

  public static void createServer(TCLIService.Processor processor) {
    try {
      TServerTransport serverTransport = new TServerSocket(Configure.gServerPort);
      // Use this for a multithreaded server
      // Use CompactProtocol
      TThreadPoolServer.Args sArgs = new TThreadPoolServer.Args(serverTransport).
          processor(processor);
      sArgs.inputProtocolFactory(new TCompactProtocol.Factory());
      sArgs.outputProtocolFactory(new TCompactProtocol.Factory());
      TServer server = new TThreadPoolServer(sArgs);
      //TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).
      //  processor(processor));

      System.out.println("Starting the QueryCache server...");
      LOG.info("Starting the QueryCache server...");
      server.serve();
    } catch (Exception e) {
      LOG.error("FATAL error : ", e);
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
    }
  }
}
