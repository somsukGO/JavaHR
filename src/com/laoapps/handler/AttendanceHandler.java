package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Attendance;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import com.laoapps.websocker.response.Response;
import com.laoapps.websocker.response.ResponseBody;
import com.laoapps.websocker.response.ResponseData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class AttendanceHandler {
    private AttendanceHandler() {
    }

    private static AttendanceHandler attendanceHandler = null;

    public static AttendanceHandler getInstance() {
        if (attendanceHandler == null) {
            attendanceHandler = new AttendanceHandler();
        }

        return attendanceHandler;
    }

    private final SessionFactory factory = HibernateConnector.getInstance().getFactory();
    private final Gson gson = new Gson();

    public String start(JsonObject data, CheckJwtResult checkJwtResult) {
        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(
                checkJwtResult.getCheckJwt().getParent())).openSession()) {

            session.beginTransaction();

            Attendance checkAttendance = (Attendance) session.createQuery("from Attendance where date = '" + MyCommon.date() + "' and endTime is null").uniqueResult();
            if (checkAttendance != null) throw new RuntimeException("Attendance already start");

            Attendance attendance = new Attendance();

            attendance.setDate(MyCommon.date());
            attendance.setStartTime(MyCommon.time());
            attendance.setByUuid(checkJwtResult.getCheckJwt().getUuid());
            if (data.has(Naming.DESCRIPTION)) attendance.setStartDescription(data.get(Naming.DESCRIPTION).getAsString());
            if (data.has(Naming.REASON)) attendance.setStartReason(data.get(Naming.REASON).getAsString());
            if (data.has(Naming.ATTACHMENT)) attendance.setStartAttachment(data.get(Naming.ATTACHMENT).getAsString());

            session.save(attendance);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new ResponseBody(Naming.attendance, Naming.startTime, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.attendance, Naming.startTime, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String end(JsonObject data, CheckJwtResult checkJwtResult) {
        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getParent())).openSession()) {

            session.beginTransaction();

            Attendance attendance = (Attendance) session.createQuery("from Attendance where date = '" + MyCommon.date() + "' and endTime is null").uniqueResult();
            if (attendance == null) throw new RuntimeException("Start attendance first");

            String endTime = MyCommon.time();

            attendance.setEndTime(endTime);
            attendance.setMinutes(MyCommon.betweenMinutes(attendance.getStartTime(), endTime));
            if (data.has(Naming.DESCRIPTION)) attendance.setEndDescription(data.get(Naming.DESCRIPTION).getAsString());
            if (data.has(Naming.REASON)) attendance.setEndReason(data.get(Naming.REASON).getAsString());
            if (data.has(Naming.ATTACHMENT)) attendance.setEndAttachment(data.get(Naming.ATTACHMENT).getAsString());

            session.update(attendance);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new ResponseBody(Naming.attendance, Naming.endTime, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.attendance, Naming.endTime, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String get(JsonObject data, CheckJwtResult checkJwtResult) {
        return "get";
    }

    public String update(JsonObject data, CheckJwtResult checkJwtResult) {
        return "update";
    }

    public String delete(JsonObject data, CheckJwtResult checkJwtResult) {
        return "delete";
    }
}

