package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Department;
import com.laoapps.database.entity.Invite;
import com.laoapps.database.entity.Personnel;
import com.laoapps.database.entity.Profiles;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.models.CheckUserJwt;
import com.laoapps.models.CheckUserJwtResult;
import com.laoapps.socket.SocketClient;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.socket.response.ResponseData;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.ArrayList;

public class InviteHandler {
    private InviteHandler() {
    }

    private static InviteHandler inviteHandler = null;

    public static InviteHandler getInstance() {
        if (inviteHandler == null) {
            inviteHandler = new InviteHandler();
        }

        return inviteHandler;
    }

    private final SessionFactory factory = HibernateConnector.getInstance().getFactory();
    private final Gson gson = new Gson();

    public String invite(JsonObject data) {

        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.INVITE, Naming.INVITE);
        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany()))
                .openSession()) {

            String position = data.get(Naming.POSITION).getAsString();
            String description = data.get(Naming.ROLE).getAsString();
            String inviteRole = data.get(Naming.ROLE).getAsString();
            String salary = data.get(Naming.SALARY).getAsString();
            String departmentUuid = data.get(Naming.departmentUuid).getAsString();
            String toUuid = data.get(Naming.toUuid).getAsString();

            if (inviteRole.equals(Naming.admin)) throw new RuntimeException("access deny");

            session.beginTransaction();

            // check role
            String role = (String) session.createQuery("select role from Personnel where userUuid = '" +
                    checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();
            if (!role.equals(Naming.admin) && !role.equals(Naming.hr)) throw new RuntimeException("access deny");

            // check department
            String checkDepartment = (String) session.createQuery("select uuid from Department where uuid = '" + departmentUuid +
                    "'").uniqueResult();
            if (checkDepartment == null) throw new RuntimeException("department not exists");

            // check toUuid
            String checkToUuid = (String) session.createQuery("select userUuid from Profiles where userUuid = '" + toUuid +
                    "'").uniqueResult();
            if (checkToUuid == null) throw new RuntimeException("to uuid not exists");

            // check invite exists or not
            String checkInvite = (String) session.createQuery("select toUuid from Invite where toUuid = '" + toUuid + "' and status = '" + Naming.pending + "' or " +
                    "status = '" + Naming.accepted + "'")
                    .uniqueResult();

            if (checkInvite != null) throw new RuntimeException("invite already exists");

            Invite invite = new Invite();
            invite.setPosition(position);
            invite.setDescription(description);
            invite.setRole(inviteRole);
            invite.setSalary(salary);
            invite.setDepartmentUuid(departmentUuid);
            invite.setCompanyUuid(checkJwtResult.getCheckJwt().getCompany());
            invite.setToUuid(toUuid);
            invite.setCreatedAt(MyCommon.currentTime());
            invite.setStatus(Naming.pending);
            invite.setUuid(MyCommon.generateUuid());

            session.save(invite);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setInvite(invite);
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new ResponseBody(Naming.INVITE, Naming.INVITE, Naming.success,
                    "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.INVITE, Naming.INVITE, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String get(JsonObject data) throws IOException {

        CheckUserJwt checkUserJwt = new CheckUserJwt(Naming.user, Naming.checkJwt, data.get(Naming.jwt).getAsString());
        JsonObject getResponse = gson.fromJson(new SocketClient().sendAndReceive(gson.toJson(checkUserJwt)),
                JsonObject.class);
        CheckUserJwtResult checkUserJwtResult = new CheckUserJwtResult(getResponse);

        if (!checkUserJwtResult.isPass()) return gson.toJson(getResponse);

        try (Session session = factory.openSession()) {

            int page = data.get(Naming.page).getAsInt();
            int limit = data.get(Naming.limit).getAsInt();

            ArrayList<Object> getKeyword = MyCommon.getKeyword(data);
            String keyword = (String) getKeyword.get(0);
            int keywordId = (int) getKeyword.get(1);

            session.beginTransaction();

            String searchQuery = "from Invite where (id = " + keywordId + " or position like '%" + keyword +
                    "%' or description like '%" + keyword + "%' or role like '%" + keyword + "%' or salary like '%" + keyword +
                    "%' or createdAt like '%" + keyword + "%' or updatedAt like '%" + keyword + "%') and toUuid = '" +
                    checkUserJwtResult.getUuid() + "' order by id desc";

            String query = (keyword == null) ? "from Invite where toUuid = '" + checkUserJwtResult.getUuid() +
                    "' order by id desc" : searchQuery;

            String countQuery = (keyword == null) ? "select count(id) from Invite" : "select count(id) " + searchQuery;
            Long count = (Long) session.createQuery(countQuery).uniqueResult();
            Integer totalPage = (count.intValue() + limit - 1) / limit;

            @SuppressWarnings("unchecked")
            ArrayList<Invite> invites = (ArrayList<Invite>) session.createQuery(query).setFirstResult((page - 1) * limit)
                    .setMaxResults(limit).getResultList();

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());
            responseData.setInvites(invites);
            responseData.setTotalPage(totalPage);
            responseData.setTotalElement(count.intValue());
            responseData.setCurrentPage(page);
            responseData.setLimit(limit);

            Response response = new Response(new ResponseBody(Naming.INVITE, Naming.get, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.INVITE, Naming.get, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String update(JsonObject data, CheckJwtResult checkJwtResult) {
        return "update";
    }

    public String delete(JsonObject data, CheckJwtResult checkJwtResult) {
        return "delete";
    }

    public String accept(JsonObject data) throws IOException {

        CheckUserJwt checkUserJwt = new CheckUserJwt(Naming.user, Naming.checkJwt, data.get(Naming.jwt).getAsString());
        JsonObject getResponse = gson.fromJson(new SocketClient().sendAndReceive(gson.toJson(checkUserJwt)),
                JsonObject.class);
        CheckUserJwtResult checkUserJwtResult = new CheckUserJwtResult(getResponse);

        if (!checkUserJwtResult.isPass()) return gson.toJson(getResponse);

        String companyUuid = data.get(Naming.companyUuid).getAsString();
        String inviteUuid = data.get(Naming.inviteUuid).getAsString();

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(companyUuid)).openSession()) {

            session.beginTransaction();

            Invite invite = (Invite) session.createQuery("from Invite where uuid = '" + inviteUuid + "' and status = '" + Naming.pending + "'").uniqueResult();
            if (invite == null) throw new RuntimeException("invite not exists");

            invite.setUpdatedAt(MyCommon.currentTime());
            invite.setAcceptedAt(MyCommon.currentTime());
            invite.setStatus(Naming.accepted);

            session.update(invite);

            Profiles profile = session.get(Profiles.class, checkUserJwtResult.getUuid());
            Personnel personnel = new Personnel();
            personnel.setFirstName(profile.getFirstName());
            personnel.setLastName(profile.getLastName());
            personnel.setPhoneNumber(profile.getPhoneNumber());
            personnel.setEmail(profile.getEmail());
            personnel.setAddress(profile.getAddress());
            personnel.setBirthDate(profile.getBirthDate());
            personnel.setIdCard(profile.getIdCard());
            personnel.setPassport(profile.getPassport());
            personnel.setCreatedAt(MyCommon.currentTime());
            personnel.setPosition(invite.getPosition());
            personnel.setRole(invite.getRole());
            personnel.setDepartmentUuid(invite.getDepartmentUuid());
            personnel.setSalary(invite.getSalary());
            personnel.setUserUuid(checkUserJwtResult.getUuid());
            personnel.setUuid(MyCommon.generateUuid());

            session.save(personnel);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());

            Response response = new Response(new ResponseBody(Naming.INVITE, Naming.INVITE, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.INVITE, Naming.INVITE, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String reject(JsonObject data) throws IOException {

        CheckUserJwt checkUserJwt = new CheckUserJwt(Naming.user, Naming.checkJwt, data.get(Naming.jwt).getAsString());
        JsonObject getResponse = gson.fromJson(new SocketClient().sendAndReceive(gson.toJson(checkUserJwt)),
                JsonObject.class);
        CheckUserJwtResult checkUserJwtResult = new CheckUserJwtResult(getResponse);

        if (!checkUserJwtResult.isPass()) return gson.toJson(getResponse);

        try (Session session = factory.openSession()) {

            String inviteUuid = data.get(Naming.inviteUuid).getAsString();

            session.beginTransaction();

            Invite invite = (Invite) session.createQuery("from Invite where uuid = '" + inviteUuid + "' and status = '" + Naming.pending + "'").uniqueResult();
            if (invite == null) throw new RuntimeException("invite not exists");

            invite.setUpdatedAt(MyCommon.currentTime());
            invite.setStatus(Naming.rejected);

            session.update(invite);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());

            Response response = new Response(new ResponseBody(Naming.INVITE, Naming.reject, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.INVITE, Naming.reject, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }
}
