package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.ProfileHandler;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;

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

    public String response(JsonObject jsonObject) {

        ProfileHandler profileHandler = ProfileHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = gson.fromJson(jsonObject.get(Naming.data), JsonObject.class);

//        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.profile, method);
//        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        switch (method) {
            case "create":
                return profileHandler.create(data, null);

            case "get":
                return profileHandler.get(data, null);

            case "update":
                return profileHandler.update(data, null);

            case "delete":
                return profileHandler.delete(data, null);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.profile, method, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
