package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.EmployeeHandler;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;

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

        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.employee, method);
        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        switch (method) {

            case "getById":
                return employeeHandler.getById(data, checkJwtResult);

            case "get":
                return employeeHandler.get(data, checkJwtResult);

            case "update":
                return employeeHandler.update(data, checkJwtResult);

            case "delete":
                return employeeHandler.delete(data, checkJwtResult);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.employee, method, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
