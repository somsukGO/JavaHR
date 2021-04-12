package com.laoapps.websocker.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.EmployeeHandler;
import com.laoapps.utils.Naming;
import com.laoapps.websocker.response.Response;
import com.laoapps.websocker.response.UtilsResponse;

public class EmployeeRequestHandler {
    private EmployeeRequestHandler() {
    }

    private static EmployeeRequestHandler employeeRequestHandler = null;

    public static EmployeeRequestHandler getInstance() {
        if (employeeRequestHandler == null) {
            employeeRequestHandler = new EmployeeRequestHandler();
        }

        return employeeRequestHandler;
    }

    private final Gson gson = new Gson();

    public String response(JsonObject jsonObject) {

        EmployeeHandler employeeHandler = EmployeeHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = gson.fromJson(jsonObject.get(Naming.data), JsonObject.class);

        switch (method) {
            case "create":
                return employeeHandler.create(data);

            case "get":
                return employeeHandler.get(data);

            case "update":
                return employeeHandler.update(data);

            case "delete":
                return employeeHandler.delete(data);

            default:
                UtilsResponse utilsResponse = new UtilsResponse(Naming.employee, Naming.unknown, 0, "Method not exists", null);
                Response response = new Response(utilsResponse);
                return new Gson().toJson(response);
        }
    }
}
