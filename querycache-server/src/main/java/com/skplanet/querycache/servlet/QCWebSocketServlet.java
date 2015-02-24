package com.skplanet.querycache.servlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Created by nazgul33 on 15. 2. 23.
 */

public class QCWebSocketServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(120*1000); // 2min
        factory.register(QCWebSocket.class);
    }
}
