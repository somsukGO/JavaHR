package com.laoapps.handler;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Attendance;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.socket.response.ResponseData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

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
                checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            String description = null, reason = null, attachment = null;

            if (data.has(Naming.DESCRIPTION)) description = data.get(Naming.DESCRIPTION).getAsString();
            if (data.has(Naming.REASON)) reason = data.get(Naming.REASON).getAsString();
            if (data.has(Naming.ATTACHMENT)) attachment = data.get(Naming.ATTACHMENT).getAsString();

            session.beginTransaction();

            Attendance checkAttendance = (Attendance) session.createQuery("from Attendance where date = '" + MyCommon.date() + "' and endTime is null").uniqueResult();
            if (checkAttendance != null) throw new RuntimeException("Attendance already start");

            Attendance attendance = new Attendance();

            attendance.setDate(MyCommon.date());
            attendance.setStartTime(MyCommon.time());
            attendance.setByUuid(checkJwtResult.getCheckJwt().getUuid());
            if (!Strings.isNullOrEmpty(description)) attendance.setStartDescription(description);
            if (!Strings.isNullOrEmpty(reason)) attendance.setStartReason(reason);
            if (!Strings.isNullOrEmpty(attachment)) attendance.setStartAttachment(attachment);

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
        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            String description = null, reason = null, attachment = null;

            if (data.has(Naming.DESCRIPTION)) description = data.get(Naming.DESCRIPTION).getAsString();
            if (data.has(Naming.REASON)) reason = data.get(Naming.REASON).getAsString();
            if (data.has(Naming.ATTACHMENT)) attachment = data.get(Naming.ATTACHMENT).getAsString();

            session.beginTransaction();

            Attendance attendance = (Attendance) session.createQuery("from Attendance where date = '" + MyCommon.date() + "' and endTime is null").uniqueResult();
            if (attendance == null) throw new RuntimeException("Start attendance first");

            String endTime = MyCommon.time();

            attendance.setEndTime(endTime);
            attendance.setMinutes(MyCommon.betweenMinutes(attendance.getStartTime(), endTime));
            if (!Strings.isNullOrEmpty(description)) attendance.setEndDescription(description);
            if (!Strings.isNullOrEmpty(reason)) attendance.setEndReason(reason);
            if (!Strings.isNullOrEmpty(attachment)) attendance.setEndAttachment(attachment);

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
        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            int page = data.get(Naming.page).getAsInt();
            int limit = data.get(Naming.limit).getAsInt();

            ArrayList<Object> getKeyword = MyCommon.getKeyword(data);
            String keyword = (String) getKeyword.get(0);
            int keywordId = (int) getKeyword.get(1);

            session.beginTransaction();

            String searchQuery = "from Attendance where id = " + keywordId + " or date like '%" + keyword +
                    "%' or startTime like '%" + keyword + "%' or endTime like '%" + keyword + "%' or minutes = " + keywordId +
                    " or byUuid like '%" + keyword + "%' order by id desc";

            String query = (keyword == null) ? "from Attendance order by id desc" : searchQuery;

            String countQuery = (keyword == null) ? "select count(id) from Attendance" : "select count(id) " + searchQuery;
            Long count = (Long) session.createQuery(countQuery).uniqueResult();
            Integer totalPage = (count.intValue() + limit - 1) / limit;

            @SuppressWarnings("unchecked")
            ArrayList<Attendance> attendances = (ArrayList<Attendance>) session.createQuery(query).setFirstResult((page - 1) * limit)
                    .setMaxResults(limit).getResultList();

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setAttendances(attendances);
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());
            responseData.setTotalPage(totalPage);
            responseData.setTotalElement(count.intValue());
            responseData.setCurrentPage(page);
            responseData.setLimit(limit);

            Response response = new Response(new ResponseBody(Naming.attendance, Naming.get, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.attendance, Naming.get, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String update(JsonObject data, CheckJwtResult checkJwtResult) {
        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            int id = data.get(Naming.ID).getAsInt();
            String date = data.get(Naming.DATE).getAsString();
            String startTime = data.get(Naming.startTime).getAsString();
            String endTime = data.get(Naming.endTime).getAsString();

            if (Strings.isNullOrEmpty(date) || Strings.isNullOrEmpty(startTime) || Strings.isNullOrEmpty(endTime))
                throw new RuntimeException("One or more field is empty or null");

            session.beginTransaction();

            Attendance attendance = session.get(Attendance.class, id);

            if (attendance == null) throw new NullPointerException("Attendance id: " + id + " not exists");

            attendance.setDate(date);
            attendance.setStartTime(startTime);
            attendance.setEndTime(endTime);
            attendance.setMinutes(MyCommon.betweenMinutes(startTime, endTime));

            session.update(attendance);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());
            responseData.setAttendance(attendance);

            Response response = new Response(new ResponseBody(Naming.attendance, Naming.update, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.attendance, Naming.update, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String delete(JsonObject data, CheckJwtResult checkJwtResult) {
        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            int id = data.get(Naming.ID).getAsInt();

            session.beginTransaction();

            String role = (String) session.createQuery("select role from Profiles where uuid = '" + checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();

            if (!role.equals(Naming.admin) && !role.equals(Naming.hr)) {
                Response response = new Response(new ResponseBody(Naming.attendance, Naming.delete, Naming.fail, "access denied", null));
                MyCommon.printMessage(response.toString());

                return gson.toJson(response);
            }

            Attendance attendance = session.get(Attendance.class, id);

            if (attendance == null) throw new NullPointerException("Attendance id: " + id + " not exists");

            session.delete(attendance);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new ResponseBody(Naming.attendance, Naming.delete, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.attendance, Naming.delete, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }
}

