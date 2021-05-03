package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Companies;
import com.laoapps.database.entity.Personnel;
import com.laoapps.database.entity.Profiles;
import com.laoapps.models.CheckUserJwt;
import com.laoapps.models.CheckUserJwtResult;
import com.laoapps.socket.SocketClient;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.socket.response.ResponseData;
import com.laoapps.utils.JWTHandler;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;

public class CompanyHandler {
    private CompanyHandler() {
    }

    private static CompanyHandler companyHandler = null;

    public static CompanyHandler getInstance() {
        if (companyHandler == null) {
            companyHandler = new CompanyHandler();
        }

        return companyHandler;
    }

    private final SessionFactory factory = HibernateConnector.getInstance().getFactory();
    private final Gson gson = new Gson();
    private final JWTHandler jwtHandler = JWTHandler.getInstance();

    public String create(JsonObject data) {

        Response response;
        String uuid = MyCommon.generateUuid();
        CheckUserJwtResult checkUserJwtResult;

        try (Session session = factory.openSession()) {

            CheckUserJwt checkUserJwt = new CheckUserJwt(Naming.user, Naming.checkJwt, data.get(Naming.jwt).getAsString());
            JsonObject getResponse = gson.fromJson(new SocketClient().sendAndReceive(gson.toJson(checkUserJwt)),
                    JsonObject.class);
            checkUserJwtResult = new CheckUserJwtResult(getResponse);

            if (!checkUserJwtResult.isPass()) return gson.toJson(getResponse);

            session.beginTransaction();

            Companies company = new Companies();

            setCompany(company, data);
            company.setOwnerUuid(checkUserJwtResult.getUuid());
            company.setCreatedAt(MyCommon.currentTime());
            company.setUuid(uuid);

            session.save(company);

            TableHandler tableHandler = TableHandler.getInstance();
            session.createSQLQuery(tableHandler.createPersonnelTable(uuid)).executeUpdate();
            session.createSQLQuery(tableHandler.createDepartmentTable(uuid)).executeUpdate();
            session.createSQLQuery(tableHandler.createAttendanceTable(uuid)).executeUpdate();

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());
            responseData.setCompany(company);

            response = new Response(new ResponseBody(Naming.company, Naming.create, Naming.success, "successful",
                    responseData));
            MyCommon.printMessage(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(new ResponseBody(Naming.company, Naming.create, Naming.fail, e.getMessage(),
                    null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

        // init personnel
        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(uuid)).openSession()) {

            session.beginTransaction();

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
            personnel.setPosition(Naming.admin);
            personnel.setRole(Naming.admin);
            personnel.setDepartmentUuid(Naming.admin);
            personnel.setSalary(Naming.admin);
            personnel.setUserUuid(checkUserJwtResult.getUuid());
            personnel.setUuid(MyCommon.generateUuid());

            session.save(personnel);

            session.getTransaction().commit();

            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(new ResponseBody(Naming.company, Naming.create, Naming.fail, e.getMessage(),
                    null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

    private void setCompany(Companies company, JsonObject data) {
        company.setName(data.get(Naming.NAME).getAsString());
        company.setDescription(data.get(Naming.DESCRIPTION).getAsString());
        company.setPhoneNumber(data.get(Naming.phoneNumber).getAsString());
        company.setEmail(data.get(Naming.EMAIL).getAsString());
        company.setFax(data.get(Naming.FAX).getAsString());
        company.setAddress(data.get(Naming.ADDRESS).getAsString());
        company.setLat(data.get(Naming.LAT).getAsFloat());
        company.setLng(data.get(Naming.LNG).getAsFloat());
        company.setAlt(data.get(Naming.ALT).getAsFloat());
    }

    public String getOwnedCompany(JsonObject data) {

        try (Session session = factory.openSession()) {

            CheckUserJwt checkUserJwt = new CheckUserJwt(Naming.user, Naming.checkJwt, data.get(Naming.jwt).getAsString());
            JsonObject getResponse = gson.fromJson(new SocketClient().sendAndReceive(gson.toJson(checkUserJwt)),
                    JsonObject.class);
            CheckUserJwtResult checkUserJwtResult = new CheckUserJwtResult(getResponse);

            if (!checkUserJwtResult.isPass()) return gson.toJson(getResponse);

            int page = data.get(Naming.page).getAsInt();
            int limit = data.get(Naming.limit).getAsInt();

            ArrayList<Object> getKeyword = MyCommon.getKeyword(data);
            String keyword = (String) getKeyword.get(0);
            int keywordId = (int) getKeyword.get(1);

            session.beginTransaction();

            String searchQuery = "from Companies where id = " + keywordId + " or name like '%" + keyword +
                    "%' or description like '%" + keyword + "%' or phoneNumber like '%" + keyword + "%' or email like '%" + keyword +
                    "%' or fax like '%" + keyword + "%' or address like '%" + keyword + "%' or createdAt like '%" + keyword + "%' order by id desc";

            String query = (keyword == null) ? "from Companies order by id desc" : searchQuery;

            String countQuery = (keyword == null) ? "select count(id) from Companies" : "select count(id) " + searchQuery;
            Long count = (Long) session.createQuery(countQuery).uniqueResult();
            Integer totalPage = (count.intValue() + limit - 1) / limit;

            @SuppressWarnings("unchecked")
            ArrayList<Companies> companies = (ArrayList<Companies>) session.createQuery(query).setFirstResult((page - 1) * limit)
                    .setMaxResults(limit).getResultList();

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());
            responseData.setCompanies(companies);
            responseData.setTotalPage(totalPage);
            responseData.setTotalElement(count.intValue());
            responseData.setCurrentPage(page);
            responseData.setLimit(limit);

            Response response = new Response(new ResponseBody(Naming.company, Naming.getOwnedCompany, Naming.success,
                    "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.company, Naming.getOwnedCompany, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String select(JsonObject data) throws IOException {

        try (Session session = factory.openSession()) {

            CheckUserJwt checkUserJwt = new CheckUserJwt(Naming.user, Naming.checkJwt, data.get(Naming.jwt).getAsString());
            JsonObject getResponse = gson.fromJson(new SocketClient().sendAndReceive(gson.toJson(checkUserJwt)),
                    JsonObject.class);
            CheckUserJwtResult checkUserJwtResult = new CheckUserJwtResult(getResponse);

            if (!checkUserJwtResult.isPass()) return gson.toJson(getResponse);

            String companyUuid = data.get(Naming.companyUuid).getAsString();

            session.beginTransaction();

            Companies company = session.get(Companies.class, companyUuid);
            if (company == null) throw new RuntimeException("company uuid not exists");

            session.getTransaction().commit();

            String companyJwt = jwtHandler.jwtEncode(checkUserJwtResult.getPhoneNumber(), checkUserJwtResult.getUuid(), companyUuid);

            try (Jedis jedis = new Jedis(Naming.HOST_NAME)) {
                jedis.hset(Naming.HR_JWT, checkUserJwtResult.getPhoneNumber(), companyJwt);
            }

            ResponseData responseData = new ResponseData();
            responseData.setCompany(company);
            responseData.setCompanyJwt(companyJwt);

            Response response = new Response(new ResponseBody(Naming.company, Naming.select, Naming.success,
                    "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.company, Naming.select,
                    Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String getJoinedCompany(JsonObject data) {
        return "getJoinedCompany";
    }

    // TODO
    public String update(JsonObject data) {
        return "update";
    }

    public String delete(JsonObject data) {
        return "delete";
    }

}
