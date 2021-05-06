package com.laoapps.socket.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.socket.SocketClient;
import com.laoapps.socket.request.handler.*;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import org.java_websocket.WebSocket;

import java.util.Map;

public class WsRequestHandler implements Runnable {

    private final Map<String, WebSocket> allOnlineUser;
    private final WebSocket webSocket;
    private final String message;

    public WsRequestHandler(Map<String, WebSocket> allOnlineUser, WebSocket webSocket, String message) {
        this.allOnlineUser = allOnlineUser;
        this.webSocket = webSocket;
        this.message = message;
    }

    @Override
    public void run() {

        MyCommon.printMessage("Request: " + message);

        Gson gson = new Gson();

        InviteRequestHandler inviteRequestHandler = InviteRequestHandler.getInstance();
        DepartmentRequestHandler departmentRequestHandler = DepartmentRequestHandler.getInstance();
        EmployeeRequestHandler employeeRequestHandler = EmployeeRequestHandler.getInstance();
        AttendanceRequestHandler attendanceRequestHandler = AttendanceRequestHandler.getInstance();
        CompanyRequestHandler companyRequestHandler = CompanyRequestHandler.getInstance();
        ProfileHandlerRequest profileHandlerRequest = ProfileHandlerRequest.getInstance();

        try {
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

            if (!jsonObject.has(Naming.object)) throw new RuntimeException("object field not exists");

            String object = jsonObject.get(Naming.object).getAsString();

            switch (object) {

                case "user":
                    webSocket.send(new SocketClient().sendAndReceive(gson.toJson(jsonObject)));
                    break;

                case "profile":
                    webSocket.send(profileHandlerRequest.response(jsonObject));
                    break;

                case "company":
                    webSocket.send(companyRequestHandler.response(jsonObject));
                    break;

                case "department":
                    webSocket.send(departmentRequestHandler.response(jsonObject));
                    break;

                case "employee":
                    webSocket.send(employeeRequestHandler.response(jsonObject));
                    break;

                case "attendance":
                    webSocket.send(attendanceRequestHandler.response(jsonObject));
                    break;

                case "invite":
                    webSocket.send(inviteRequestHandler.response(jsonObject));
                    break;

                // TODO:
                case "job":
                    break;

                case "dailyJob":
                    break;

                case "trace":
                    break;

                default:
                    Response response = new Response(new ResponseBody(Naming.unknown, Naming.unknown, Naming.fail,
                            "object not exists", null));
                    MyCommon.printMessage(response.toString());
                    webSocket.send(gson.toJson(response));
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.unknown, Naming.unknown, Naming.fail,
                    e.getMessage(), null));
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
