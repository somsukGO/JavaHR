package com.laoapps.websocker;

import com.laoapps.handler.TableHandler;
import com.laoapps.handler.UsersHandler;
import com.laoapps.websocker.request.WSRequestHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WSServer extends WebSocketServer {

    private final int THREAD_POOL_SIZE = 500;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    public static final Map<String, WebSocket> allOnlineUser = new HashMap<>();

    public WSServer(int port) {
        super(new InetSocketAddress(port));
        TableHandler.getInstance().initTable();
        initHandler();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println(webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " connected");
        printConnection();
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        printConnection();
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        threadPool.execute(new WSRequestHandler(allOnlineUser, webSocket, message));
    }

    @Override
    public void onError(WebSocket webSocket, Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocketServer started on port: " + this.getPort());
    }

    private void printConnection() {
        System.out.println("-".repeat(25));
        System.out.println("Connection count: " + this.getConnections().size());
        System.out.println("Currently online user: " + allOnlineUser.size());
        System.out.println("-".repeat(25));
    }

    private void initHandler() {
        UsersHandler.getInstance();
    }
}
