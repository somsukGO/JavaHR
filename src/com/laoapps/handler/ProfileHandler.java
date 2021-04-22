package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Profiles;
import com.laoapps.models.CheckUserJwtResult;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.socket.response.ResponseData;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ProfileHandler {
    private ProfileHandler() {
    }

    private static ProfileHandler profileHandler = null;

    public static ProfileHandler getInstance() {
        if (profileHandler == null) {
            profileHandler = new ProfileHandler();
        }

        return profileHandler;
    }

    private final SessionFactory factory = HibernateConnector.getInstance().getFactory();
    private final Gson gson = new Gson();

    public String create(JsonObject data, CheckUserJwtResult checkUserJwtResult) {
        try (Session session = factory.openSession()) {

            session.beginTransaction();

            Profiles getProfile = session.get(Profiles.class, checkUserJwtResult.getUuid());
            if (getProfile != null) throw new RuntimeException("profile already exists");

            Profiles profiles = new Profiles();
            profiles.setPhoneNumber(checkUserJwtResult.getPhoneNumber());
            profiles.setUuid(checkUserJwtResult.getUuid());
            setProfile(data, profiles);

            session.save(profiles);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());
            responseData.setProfile(profiles);

            Response response = new Response(new ResponseBody(Naming.profile, Naming.create, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.profile, Naming.create, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    private void setProfile(JsonObject data, Profiles profiles) {
        profiles.setFirstName(data.get(Naming.firstName).getAsString());
        profiles.setLastName(data.get(Naming.lastName).getAsString());
        profiles.setEmail(data.get(Naming.EMAIL).getAsString());
        profiles.setAddress(data.get(Naming.ADDRESS).getAsString());
        profiles.setBirthDate(data.get(Naming.birthDate).getAsString());
        profiles.setIdCard(data.get(Naming.idCard).getAsString());
        profiles.setPassport(data.get(Naming.PASSPORT).getAsString());
        profiles.setCreatedAt(MyCommon.currentTime());
        profiles.setAge(profiles.getAge());
    }

    public String get(CheckUserJwtResult checkUserJwtResult) {
        try (Session session = factory.openSession()) {

            session.beginTransaction();

            Profiles profile = session.get(Profiles.class, checkUserJwtResult.getUuid());

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());
            if (profile != null) profile.setAge(profile.getAge());
            responseData.setProfile(profile);

            Response response = new Response(new ResponseBody(Naming.profile, Naming.get, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.profile, Naming.get, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String update(JsonObject data, CheckUserJwtResult checkUserJwtResult) {
        try (Session session = factory.openSession()) {

            session.beginTransaction();

            Profiles profile = session.get(Profiles.class, checkUserJwtResult.getUuid());
            if (profile == null) throw new RuntimeException("profile not exists");

            setProfile(data, profile);
            profile.setUpdatedAt(MyCommon.currentTime());

            session.update(profile);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());
            responseData.setProfile(profile);

            Response response = new Response(new ResponseBody(Naming.profile, Naming.update, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.profile, Naming.update, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

}
