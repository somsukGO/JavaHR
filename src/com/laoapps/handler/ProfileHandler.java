package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.models.CheckJwtResult;
import org.hibernate.SessionFactory;

public class ProfileHandler {
    private ProfileHandler() {}

    private static ProfileHandler profileHandler = null;

    public static ProfileHandler getInstance() {
        if (profileHandler == null) {
            profileHandler = new ProfileHandler();
        }

        return profileHandler;
    }

    private final SessionFactory factory = HibernateConnector.getInstance().getFactory();
    private final Gson gson = new Gson();

    public String create(JsonObject data, CheckJwtResult checkJwtResult) {
        return "create";
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
