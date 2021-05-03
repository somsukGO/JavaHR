package com.laoapps.socket;

import com.laoapps.handler.*;
import com.laoapps.socket.request.WsRequestHandler;
import com.laoapps.socket.request.handler.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Websocket extends WebSocketServer {

    private final int THREAD_POOL_SIZE = 500;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    public static final Map<String, WebSocket> allOnlineUser = new HashMap<>();

    public Websocket(int port) {
        super(new InetSocketAddress(port));
//        TableHandler.getInstance().initTable();
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
        threadPool.execute(new WsRequestHandler(allOnlineUser, webSocket, message));
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
        AttendanceRequestHandler.getInstance();
        CompanyRequestHandler.getInstance();
        DepartmentRequestHandler.getInstance();
        EmployeeRequestHandler.getInstance();
        InviteRequestHandler.getInstance();
        ProfileHandlerRequest.getInstance();

        AttendanceHandler.getInstance();
        CompanyHandler.getInstance();
        DepartmentHandler.getInstance();
        EmployeeHandler.getInstance();
        InviteHandler.getInstance();
        ProfileHandler.getInstance();
    }
}
