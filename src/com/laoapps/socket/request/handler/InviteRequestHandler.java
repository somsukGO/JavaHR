package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.InviteHandler;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.utils.Naming;

import java.io.IOException;

public class InviteRequestHandler {
    private InviteRequestHandler() {
    }

    private static InviteRequestHandler inviteRequestHandler = null;

    public static InviteRequestHandler getInstance() {
        if (inviteRequestHandler == null) {
            inviteRequestHandler = new InviteRequestHandler();
        }

        return inviteRequestHandler;
    }

    private final Gson gson = new Gson();

    public String response(JsonObject jsonObject) throws IOException {

        InviteHandler inviteHandler = InviteHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = gson.fromJson(jsonObject.get(Naming.data), JsonObject.class);

        switch (method) {
            case "invite":
                return inviteHandler.invite(data);

            case "get":
                return inviteHandler.get(data);

            case "accept":
                return inviteHandler.accept(data);

            case "reject":
                return inviteHandler.reject(data);

            // TODO: later
            case "update":
                return inviteHandler.update(data, null);

            case "delete":
                return inviteHandler.delete(data);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.INVITE, method, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
