package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.CompanyHandler;
import com.laoapps.models.CheckUserJwt;
import com.laoapps.models.CheckUserJwtResult;
import com.laoapps.socket.SocketClient;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.utils.Naming;

import java.io.IOException;

public class CompanyRequestHandler {
    private CompanyRequestHandler() {}

    private static CompanyRequestHandler companyRequestHandler = null;

    public static CompanyRequestHandler getInstance() {
        if (companyRequestHandler == null) {
            companyRequestHandler = new CompanyRequestHandler();
        }

        return companyRequestHandler;
    }

    private final Gson gson = new Gson();

    public String response(JsonObject jsonObject) throws IOException {

        CompanyHandler companyHandler = CompanyHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = jsonObject.getAsJsonObject(Naming.data);
        String jwt = data.get(Naming.jwt).getAsString();

        CheckUserJwt checkUserJwt = new CheckUserJwt(Naming.user, Naming.checkJwt, jwt);
        JsonObject getResponse = gson.fromJson(new SocketClient().sendAndReceive(gson.toJson(checkUserJwt)), JsonObject.class);
        CheckUserJwtResult checkUserJwtResult = new CheckUserJwtResult(getResponse);

        switch (method) {
            case "create":
                return companyHandler.create(data, checkUserJwtResult);

            case "get":
                return companyHandler.get(data, checkUserJwtResult);

            case "update":
                return companyHandler.update(data, checkUserJwtResult);

            case "delete":
                return companyHandler.delete(data, checkUserJwtResult);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.company, method, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
