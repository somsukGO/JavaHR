package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.CompanyHandler;
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

    public String response(JsonObject jsonObject) throws IOException {

        CompanyHandler companyHandler = CompanyHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = jsonObject.getAsJsonObject(Naming.data);

        switch (method) {
            case "create":
                return companyHandler.create(data);

            case "select":
                return companyHandler.select(data);

            case "getOwnedCompany":
                return companyHandler.getOwnedCompany(data);

            case "getJoinedCompany":
                return companyHandler.getJoinedCompany(data);

            case "update":
                return companyHandler.update(data);

            case "delete":
                return companyHandler.delete(data);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.company, method, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
