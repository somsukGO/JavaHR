package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.CompanyHandler;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;

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

    public String response(JsonObject jsonObject) {

        CompanyHandler companyHandler = CompanyHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = gson.fromJson(jsonObject.get(Naming.data), JsonObject.class);

        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.COMPANY, method);
        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        switch (method) {
            case "create":
                return companyHandler.create(data, checkJwtResult);

            case "get":
                return companyHandler.get(data, checkJwtResult);

            case "update":
                return companyHandler.update(data, checkJwtResult);

            case "delete":
                return companyHandler.delete(data, checkJwtResult);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.COMPANY, method, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
