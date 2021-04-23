package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.models.CheckUserJwt;
import com.laoapps.models.CheckUserJwtResult;
import com.laoapps.socket.SocketClient;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.utils.Naming;
import com.laoapps.handler.ProfileHandler;

import java.io.IOException;

public class ProfileHandlerRequest {
    private ProfileHandlerRequest() {
    }

    private static ProfileHandlerRequest profileHandlerRequest = null;

    public static ProfileHandlerRequest getInstance() {
        if (profileHandlerRequest == null) {
            profileHandlerRequest = new ProfileHandlerRequest();
        }

        return profileHandlerRequest;
    }

    private final Gson gson = new Gson();

    public String response(JsonObject jsonObject) throws IOException {

        ProfileHandler profileHandler = ProfileHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = jsonObject.getAsJsonObject(Naming.data);
        String jwt = data.get(Naming.jwt).getAsString();

        CheckUserJwt checkUserJwt = new CheckUserJwt(Naming.user, Naming.checkJwt, jwt);
        JsonObject getResponse = gson.fromJson(new SocketClient().sendAndReceive(gson.toJson(checkUserJwt)), JsonObject.class);
        CheckUserJwtResult checkUserJwtResult = new CheckUserJwtResult(getResponse);

        if (!checkUserJwtResult.isPass()) return gson.toJson(getResponse);

        switch (method) {
            case "create":
                return profileHandler.create(data, checkUserJwtResult);

            case "get":
                return profileHandler.get(checkUserJwtResult);

            case "update":
                return profileHandler.update(data, checkUserJwtResult);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.profile, method, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
