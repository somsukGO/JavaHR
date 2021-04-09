package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.entity.Profiles;
import com.laoapps.utils.JWTHandler;
import com.laoapps.utils.MyCommon;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Users;
import com.laoapps.utils.Naming;
import com.laoapps.websocker.response.ResponseData;
import com.laoapps.websocker.response.Response;
import com.laoapps.websocker.response.UtilsResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import redis.clients.jedis.Jedis;

public class UsersHandler {
    private UsersHandler() {
    }

    private static UsersHandler usersHandler = null;

    public static UsersHandler getInstance() {
        if (usersHandler == null) {
            usersHandler = new UsersHandler();
        }

        return usersHandler;
    }

    private final Gson gson = new Gson();
    private final SessionFactory factory = HibernateConnector.getInstance().getFactory();
    private final JWTHandler jwtHandler = JWTHandler.getInstance();

    public String register(JsonObject registerRequest) {

        try (Session session = factory.openSession()) {

            String phoneNumber = registerRequest.get(Naming.phoneNumber).getAsString();
            String password = registerRequest.get(Naming.PASSWORD).getAsString();

            session.beginTransaction();

            Users getUser = (Users) session.createQuery("from Users where phoneNumber = '" + phoneNumber + "'").uniqueResult();
            if (getUser != null) {
                Response response = new Response(new UtilsResponse(Naming.user, Naming.register, Naming.fail, "Phone number already exists", null));
                MyCommon.printMessage(response.toString());

                return gson.toJson(response);
            }

            String hashedPassword = MyCommon.hashPassword(password);
            String createdAt = MyCommon.currentTime();
            String uuid = MyCommon.generateUuid();
            String signature = MyCommon.generateSignature();

            Users user = new Users();
            user.setPhoneNumber(phoneNumber);
            user.setPassword(hashedPassword);
            user.setCreatedAt(createdAt);
            user.setUuid(uuid);
            user.setParent(uuid);
            user.setRole(Naming.admin);
            user.setSignature(signature);

            session.persist(user);

            TableHandler tableHandler = TableHandler.getInstance();
            tableHandler.createTable(uuid, session);

            session.getTransaction().commit();

            Response response = new Response(new UtilsResponse(Naming.user, Naming.register, Naming.success, "Register successful", null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);

        } catch (Exception e) {

            Response response = new Response(new UtilsResponse(Naming.user, Naming.register, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());
            e.printStackTrace();

            return gson.toJson(response);

        }
    }

    public String login(JsonObject loginRequest) {

        try (Session session = factory.openSession()) {

            session.beginTransaction();

            String phoneNumber = loginRequest.get(Naming.phoneNumber).getAsString();
            String hashedPassword = MyCommon.hashPassword(loginRequest.get(Naming.PASSWORD).getAsString());

            Users user = (Users) session.createQuery("from Users where phoneNumber = '" + phoneNumber +
                    "' and password = '" + hashedPassword + "'").uniqueResult();

            if (user == null) {
                Response response = new Response(new UtilsResponse(Naming.user, Naming.login, Naming.fail, "Phone number or password incorrect", null));
                MyCommon.printMessage(response.toString());

                return gson.toJson(response);
            }

            Profiles profile = session.get(Profiles.class, user.getUuid());

            session.getTransaction().commit();

            String jwt = jwtHandler.jwtEncode(phoneNumber, user.getUuid(), user.getParent());

            try (Jedis jedis = new Jedis(Naming.HOST_NAME)) {
                jedis.hset(Naming.HR_JWT, phoneNumber, jwt);
            }

            ResponseData responseData = new ResponseData();
            user.setPassword(null);
            responseData.setUser(user);
            responseData.setJwt(jwt);
            responseData.setProfile(profile);

            Response response = new Response(new UtilsResponse(Naming.user, Naming.login, Naming.success, "Login successful", responseData));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new UtilsResponse(Naming.user, Naming.login, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

}
