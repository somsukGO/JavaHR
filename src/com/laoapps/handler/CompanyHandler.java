package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Companies;
import com.laoapps.models.CheckUserJwtResult;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.socket.response.ResponseData;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.NaturalId;

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

    public String create(JsonObject data, CheckUserJwtResult checkUserJwtResult) {
        try (Session session = factory.openSession()) {

            session.beginTransaction();

            Companies company = new Companies();
            company.setName(data.get(Naming.NAME).getAsString());
            company.setDescription(data.get(Naming.DESCRIPTION).getAsString());
            company.setPhoneNumber(data.get(Naming.phoneNumber).getAsString());
            company.setEmail(data.get(Naming.EMAIL).getAsString());
            company.setFax(data.get(Naming.FAX).getAsString());
            company.setOwnerUuid(checkUserJwtResult.getUuid());
            company.setAddress(data.get(Naming.ADDRESS).getAsString());
            company.setLat(data.get(Naming.LAT).getAsFloat());
            company.setLng(data.get(Naming.LNG).getAsFloat());
            company.setAlt(data.get(Naming.ALT).getAsFloat());
            company.setCreatedAt(MyCommon.currentTime());
            company.setUuid(MyCommon.generateUuid());

            session.save(company);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());
            responseData.setCompany(company);

            Response response = new Response(new ResponseBody(Naming.company, Naming.create, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.company, Naming.create, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String get(JsonObject data, CheckUserJwtResult checkJwtResult) {
        return "get";
    }

    public String update(JsonObject data, CheckUserJwtResult checkJwtResult) {
        return "update";
    }

    public String delete(JsonObject data, CheckUserJwtResult checkJwtResult) {
        return "delete";
    }
}
