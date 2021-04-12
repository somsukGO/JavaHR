package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.HibernateConnector;
import org.hibernate.SessionFactory;

public class EmployeeHandler {
    private EmployeeHandler() {
    }

    private static EmployeeHandler employeeHandler = null;

    public static EmployeeHandler getInstance() {
        if (employeeHandler == null) {
            employeeHandler = new EmployeeHandler();
        }

        return employeeHandler;
    }

    private final SessionFactory factory = HibernateConnector.getInstance().getFactory();
    private final Gson gson = new Gson();

    public String create(JsonObject data) {
        return "create";
    }

    public String get(JsonObject data) {
        return "get";
    }

    public String update(JsonObject data) {
        return "update";
    }

    public String delete(JsonObject data) {
        return "delete";
    }
}
