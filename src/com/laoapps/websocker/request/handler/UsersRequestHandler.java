package com.laoapps.websocker.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.UsersHandler;
import com.laoapps.utils.Naming;
import com.laoapps.websocker.response.Response;
import com.laoapps.websocker.response.ResponseBody;

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

    private final Gson gson = new Gson();

    public String response(JsonObject jsonObject) {

        UsersHandler usersHandler = UsersHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = gson.fromJson(jsonObject.get(Naming.data), JsonObject.class);

        switch (method) {
            case "register":
                return usersHandler.register(data);

            case "login":
                return usersHandler.login(data);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.user, Naming.unknown, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
