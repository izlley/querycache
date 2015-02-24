package com.skplanet.querycache.servlet;

import com.google.gson.Gson;
import com.skplanet.querycache.server.util.RuntimeProfile;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nazgul33 on 15. 2. 23.
 */
public class QCWebSocket implements WebSocketListener {
    private static final boolean DEBUG = false;
    private static final Logger LOG = LoggerFactory.getLogger("websocket");

    private Session outbound;
    private int id = -1;

    RuntimeProfile rp = RuntimeProfile.getInstance();

    private static class RequestMsg {
        public String request;
        public String channel;
        public String data;
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len)
    {
        /* only interested in test messages */
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        this.outbound = null;
        if ( this.id >= 0 ) {
            rp.removeSubscriber(id);
        }
        this.id = -1;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        this.outbound = session;
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        LOG.error("websocket error", cause);
    }

    @Override
    public void onWebSocketText(String message) {
        if (outbound == null || !outbound.isOpen())
            return;

        Gson gson = new Gson();
        RequestMsg msg = gson.fromJson(message, RequestMsg.class);
        switch (msg.request) {
            case "subscribe": {
                if ("runningQueries".equals(msg.channel)) {
                    id = rp.addSubscriber(this);
                    outbound.getRemote().sendString("{\"msgType\":\"result\", \"result\":\"ok\"}", null);
                } else {
                    outbound.getRemote().sendString("{\"msgType\":\"result\", \"result\":\"failure\"}", null);
                }
                return;
            }
            case "ping": {
                outbound.getRemote().sendString("{\"msgType\":\"pong\"}", null);
                return;
            }
        }

        LOG.error("unknown message " + message);
    }

    public void sendMessage(String message) {
        if (outbound == null || !outbound.isOpen() || id < 0)
            return;
        outbound.getRemote().sendString(message, null);
    }
}
