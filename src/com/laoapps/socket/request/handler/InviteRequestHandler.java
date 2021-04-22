package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.InviteHandler;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;

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

    public String response(JsonObject jsonObject) {

        InviteHandler inviteHandler = InviteHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = gson.fromJson(jsonObject.get(Naming.data), JsonObject.class);

//        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.INVITE, method);
//        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        switch (method) {
            case "invite":
                return inviteHandler.invite(data, null);

            case "get":
                return inviteHandler.get(data, null);

            case "update":
                return inviteHandler.update(data, null);

            case "delete":
                return inviteHandler.delete(data, null);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.INVITE, method, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
