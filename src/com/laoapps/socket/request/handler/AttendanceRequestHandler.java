package com.laoapps.socket.request.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.handler.AttendanceHandler;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;

public class AttendanceRequestHandler {
    private AttendanceRequestHandler() {
    }

    private static AttendanceRequestHandler attendanceRequestHandler = null;

    public static AttendanceRequestHandler getInstance() {
        if (attendanceRequestHandler == null) {
            attendanceRequestHandler = new AttendanceRequestHandler();
        }

        return attendanceRequestHandler;
    }

    private final Gson gson = new Gson();

    public String response(JsonObject jsonObject) {

        AttendanceHandler attendanceHandler = AttendanceHandler.getInstance();

        String method = jsonObject.get(Naming.method).getAsString();
        JsonObject data = gson.fromJson(jsonObject.get(Naming.data), JsonObject.class);

        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.attendance, method);
        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        switch (method) {
            case "start":
                return attendanceHandler.start(data, checkJwtResult);

            case "end":
                return attendanceHandler.end(data, checkJwtResult);

            case "get":
                return attendanceHandler.get(data, checkJwtResult);

            case "update":
                return attendanceHandler.update(data, checkJwtResult);

            case "delete":
                return attendanceHandler.delete(data, checkJwtResult);

            default:
                ResponseBody responseBody = new ResponseBody(Naming.attendance, method, 0, "method not exists", null);
                Response response = new Response(responseBody);
                return new Gson().toJson(response);
        }
    }
}
