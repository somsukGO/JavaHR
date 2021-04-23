package com.laoapps.handler;

import com.google.common.base.Strings;
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

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            profiles.setUserUuid(checkUserJwtResult.getUuid());
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
        String email = null, idCard = null, passport = null;

        if (data.has(Naming.EMAIL)) email = data.get(Naming.EMAIL).getAsString();
        if (data.has(Naming.idCard)) idCard = data.get(Naming.idCard).getAsString();
        if (data.has(Naming.PASSPORT)) passport = data.get(Naming.PASSPORT).getAsString();

        if (!Strings.isNullOrEmpty(email)) {
            String regex = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) throw new RuntimeException("email invalid");
            profiles.setEmail(email);
        }

        String birthDate = data.get(Naming.birthDate).getAsString();
        if (LocalDate.parse(birthDate).isAfter(LocalDate.now())) throw new RuntimeException("birth date invalid");

        profiles.setFirstName(data.get(Naming.firstName).getAsString());
        profiles.setLastName(data.get(Naming.lastName).getAsString());
        profiles.setAddress(data.get(Naming.ADDRESS).getAsString());
        profiles.setBirthDate(data.get(Naming.birthDate).getAsString());
        if (!Strings.isNullOrEmpty(idCard)) profiles.setIdCard(idCard);
        if (!Strings.isNullOrEmpty(passport)) profiles.setPassport(passport);
        profiles.setCreatedAt(MyCommon.currentTime());
        profiles.calculateAge();
    }

    public String get(CheckUserJwtResult checkUserJwtResult) {
        try (Session session = factory.openSession()) {

            session.beginTransaction();

            Profiles profile = session.get(Profiles.class, checkUserJwtResult.getUuid());

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkUserJwtResult.getJwt());
            if (profile != null) profile.calculateAge();
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
