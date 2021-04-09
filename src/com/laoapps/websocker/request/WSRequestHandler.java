package com.laoapps.websocker.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import com.laoapps.websocker.response.Response;
import com.laoapps.websocker.response.UtilsResponse;
import org.java_websocket.WebSocket;

import java.util.Map;

public class WSRequestHandler implements Runnable {

    private final Map<String, WebSocket> allOnlineUser;
    private final WebSocket webSocket;
    private final String message;

    public WSRequestHandler(Map<String, WebSocket> allOnlineUser, WebSocket webSocket, String message) {
        this.allOnlineUser = allOnlineUser;
        this.webSocket = webSocket;
        this.message = message;
    }

    @Override
    public void run() {

        MyCommon.printMessage("Request: " + message);

        Gson gson = new Gson();
        UsersRequestHandler usersRequestHandler = UsersRequestHandler.getInstance();
        DepartmentRequestHandler departmentRequestHandler = DepartmentRequestHandler.getInstance();

        try {
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

            String object = jsonObject.get(Naming.object).getAsString();

            switch (object) {

                case "user":
                    webSocket.send(usersRequestHandler.response(jsonObject));
                    break;

                case "department":
                    webSocket.send(departmentRequestHandler.response(jsonObject));
                    break;

                default:
                    Response response = new Response(new UtilsResponse(Naming.unknown, Naming.unknown, Naming.fail,
                            "object not exists", null));
                    MyCommon.printMessage(response.toString());
                    webSocket.send(gson.toJson(response));
                    break;
            }

        } catch (Exception e) {
            Response response = new Response(new UtilsResponse(Naming.unknown, Naming.unknown, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            webSocket.send(gson.toJson(response));
        }
    }

    private void printCurrentlyOnlineUser() {
        System.out.println("-".repeat(25));
        System.out.println("Currently online user: " + allOnlineUser.size());
        System.out.println("-".repeat(25));
    }

}
