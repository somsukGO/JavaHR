package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Department;
import com.laoapps.database.entity.Invite;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.socket.response.ResponseData;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

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

    public String invite(JsonObject data, CheckJwtResult checkJwtResult) {
        return "invite";
    }

    public String get(JsonObject data, CheckJwtResult checkJwtResult) {
        try (Session session = factory.openSession()) {

            int page = data.get(Naming.page).getAsInt();
            int limit = data.get(Naming.limit).getAsInt();

            ArrayList<Object> getKeyword = MyCommon.getKeyword(data);
            String keyword = (String) getKeyword.get(0);
            int keywordId = (int) getKeyword.get(1);

            session.beginTransaction();

            String searchQuery = "from Invite where id = " + keywordId + " or position like '%" + keyword +
                    "%' or description like '%" + keyword + "%' or role like '%" + keyword + "%' or salary like '%" + keyword +
                    "%' or createdAt like '%" + keyword + "%' or updatedAt like '%" + keyword + "%' order by id desc";

            String query = (keyword == null) ? "from Invite order by id desc" : searchQuery;

            String countQuery = (keyword == null) ? "select count(id) from Invite" : "select count(id) " + searchQuery;
            Long count = (Long) session.createQuery(countQuery).uniqueResult();
            Integer totalPage = (count.intValue() + limit - 1) / limit;

            @SuppressWarnings("unchecked")
            ArrayList<Invite> invites = (ArrayList<Invite>) session.createQuery(query).setFirstResult((page - 1) * limit)
                    .setMaxResults(limit).getResultList();

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt("test");
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
}
