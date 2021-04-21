package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.UsersHandler;
import com.laoapps.utils.Naming;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;

import java.io.IOException;

public class UsersRequestHandler {

    private UsersRequestHandler() {
    }

    private static UsersRequestHandler usersRequestHandler = null;

    public static UsersRequestHandler getInstance() {
        if (usersRequestHandler == null) {
            usersRequestHandler = new UsersRequestHandler();
        }

        return usersRequestHandler;
    }

    public String response(JsonObject jsonObject) throws IOException {

        UsersHandler usersHandler = UsersHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();

        switch (method) {
            case "register":
                return usersHandler.register(jsonObject);

            case "login":
                return usersHandler.login(jsonObject);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.user, Naming.unknown, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
