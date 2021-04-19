package com.laoapps.websocker.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.DepartmentHandler;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import com.laoapps.websocker.response.Response;
import com.laoapps.websocker.response.ResponseBody;

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

        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.department, method);
        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        switch (method) {
            case "create":
                return departmentHandler.create(data, checkJwtResult);

            case "get":
                return departmentHandler.get(data, checkJwtResult);

            case "update":
                return departmentHandler.update(data, checkJwtResult);

            case "delete":
                return departmentHandler.delete(data, checkJwtResult);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.department, Naming.unknown, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
