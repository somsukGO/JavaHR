package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.socket.response.ResponseData;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

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

    public String invite(JsonObject data, CheckJwtResult checkJwtResult) {
        return "invite";
    }

    public String get(JsonObject data, CheckJwtResult checkJwtResult) {
        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            session.beginTransaction();

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

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
}
