package com.laoapps.websocker.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.DepartmentHandler;
import com.laoapps.utils.Naming;
import com.laoapps.websocker.response.Response;
import com.laoapps.websocker.response.UtilsResponse;

public class DepartmentRequestHandler {
    private DepartmentRequestHandler() {
    }

    private static DepartmentRequestHandler departmentRequestHandler = null;

    public static DepartmentRequestHandler getInstance() {
        if (departmentRequestHandler == null) {
            departmentRequestHandler = new DepartmentRequestHandler();
        }

        return departmentRequestHandler;
    }

    private final Gson gson = new Gson();

    public String response(JsonObject jsonObject) {

        DepartmentHandler departmentHandler = DepartmentHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = gson.fromJson(jsonObject.get(Naming.data), JsonObject.class);

        switch (method) {
            case "create":
                return departmentHandler.create(data);

            case "get":
                return departmentHandler.get(data);

            case "update":
                return departmentHandler.update(data);

            case "delete":
                return departmentHandler.delete(data);

            default:
                UtilsResponse utilsResponse = new UtilsResponse(Naming.department, Naming.unknown, 0, "Method not exists", null);
                Response response = new Response(utilsResponse);
                return new Gson().toJson(response);
        }
    }
}
