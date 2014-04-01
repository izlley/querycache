package com.skplanet.querycache.server;

import java.net.SocketException;

import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * TServerSocketKeepAlive - like TServerSocket, but will enable keepalive for
 * accepted sockets.
 *
 */
public class TServerSocketKeepAlive extends TServerSocket {
  public TServerSocketKeepAlive(int port) throws TTransportException {
    // timeout = 0
    super(port, 0);
  }

  @Override
  protected TSocket acceptImpl() throws TTransportException {
    TSocket ts = super.acceptImpl();
    try {
      ts.getSocket().setKeepAlive(true);
    } catch (SocketException e) {
      throw new TTransportException(e);
    }
    return ts;
  }
}
