package com.laoapps.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Department;
import com.laoapps.database.entity.Users;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import com.laoapps.websocker.response.Response;
import com.laoapps.websocker.response.ResponseData;
import com.laoapps.websocker.response.UtilsResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

public class DepartmentHandler {
    private DepartmentHandler() {
    }

    private static DepartmentHandler departmentHandler = null;

    public static DepartmentHandler getInstance() {
        if (departmentHandler == null) {
            departmentHandler = new DepartmentHandler();
        }

        return departmentHandler;
    }

    private final SessionFactory factory = HibernateConnector.getInstance().getFactory();
    private final Gson gson = new Gson();

    public String create(JsonObject data) {

        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.department, Naming.create);

        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getParent())).openSession()) {

            String name = data.get(Naming.NAME).getAsString();
            String parent = data.get(Naming.PARENT).getAsString();
            String remark = null;

            if (data.has(Naming.REMARK)) remark = data.get(Naming.REMARK).getAsString();

            session.beginTransaction();

            String role = (String) session.createQuery("select role from Users where uuid = '" + checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();

            if (!role.equals(Naming.admin) && !role.equals(Naming.hr)) {
                Response response = new Response(new UtilsResponse(Naming.department, Naming.create, Naming.fail, "Access denied", null));
                MyCommon.printMessage(response.toString());

                return gson.toJson(response);
            }

            Department department = new Department();

            department.setName(name);
            department.setParent(parent);
            if (remark != null) department.setRemark(remark);
            department.setCreatedAt(MyCommon.currentTime());
            department.setUuid(MyCommon.generateUuid());

            session.save(department);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setDepartment(department);
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new UtilsResponse(Naming.department, Naming.create, Naming.success,
                    "Create department successful", responseData));

            MyCommon.printMessage(response.toString());

            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new UtilsResponse(Naming.department, Naming.create, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

    public String get(JsonObject data) {

        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.department, Naming.get);

        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getParent())).openSession()) {

            int page = data.get(Naming.page).getAsInt();
            int limit = data.get(Naming.limit).getAsInt();

            String keyword = null;
            int keywordId = 0;

            if (data.has(Naming.keyword)) {
                if (!data.get(Naming.keyword).getAsString().isBlank()) {
                    keyword = data.get(Naming.keyword).getAsString();
                    try {
                        keywordId = Integer.parseInt(keyword);
                    } catch (Exception e) {
                        keywordId = 0;
                    }
                }
            }

            session.beginTransaction();

            String query = (keyword == null) ? "from Department" : "from Department where id like " + keywordId + " or name like '" + keyword +
                            "' or parent like '" + keyword + "' or remark like '" + keyword + "' or createdAt like '" + keyword +
                            "' or updatedAt like '" + keyword + "' or uuid like '" + keyword + "'";

            Long count = (Long) session.createQuery("select count(id) from Department").uniqueResult();
            Integer totalPage = (count.intValue() + limit - 1) / limit;

            @SuppressWarnings("unchecked")
            ArrayList<Department> departments = (ArrayList<Department>) session.createQuery(query)
                    .setFirstResult((page - 1) * limit).setMaxResults(limit).getResultList();

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setDepartments(departments);
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());
            responseData.setTotalPage(totalPage);

            Response response = new Response(new UtilsResponse(Naming.department, Naming.get, Naming.success,
                    "Get department successful", responseData));

            MyCommon.printMessage(response.toString());

            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new UtilsResponse(Naming.department, Naming.get, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

    public String update(JsonObject data) {

        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.department, Naming.update);

        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getParent())).openSession()) {

            session.beginTransaction();

            String role = (String) session.createQuery("select role from Users where uuid = '" + checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();

            if (!role.equals(Naming.admin) && !role.equals(Naming.hr)) {
                Response response = new Response(new UtilsResponse(Naming.department, Naming.update, Naming.fail, "Access denied", null));
                MyCommon.printMessage(response.toString());

                return gson.toJson(response);
            }

            Department department = session.get(Department.class, data.get(Naming.UUID).getAsString());

            if (data.has(Naming.NAME)) department.setName(data.get(Naming.NAME).getAsString());
            if (data.has(Naming.PARENT)) department.setParent(data.get(Naming.PARENT).getAsString());
            if (data.has(Naming.REMARK)) department.setRemark(data.get(Naming.REMARK).getAsString());
            department.setUpdatedAt(MyCommon.currentTime());

            session.update(department);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setDepartment(department);
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new UtilsResponse(Naming.department, Naming.update, Naming.success,
                    "Update department successful", responseData));

            MyCommon.printMessage(response.toString());

            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new UtilsResponse(Naming.department, Naming.update, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

    public String delete(JsonObject data) {

        CheckJwtResult checkJwtResult = MyCommon.checkJwtResult(data, Naming.department, Naming.delete);

        if (!checkJwtResult.isPass()) return gson.toJson(checkJwtResult.getResponse());

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getParent())).openSession()) {

            session.beginTransaction();

            String role = (String) session.createQuery("select role from Users where uuid = '" + checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();

            if (!role.equals(Naming.admin) && !role.equals(Naming.hr)) {
                Response response = new Response(new UtilsResponse(Naming.department, Naming.delete, Naming.fail, "Access denied", null));
                MyCommon.printMessage(response.toString());

                return gson.toJson(response);
            }

            Department department = session.get(Department.class, data.get(Naming.UUID).getAsString());

            session.delete(department);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new UtilsResponse(Naming.department, Naming.delete, Naming.success,
                    "Delete department successful", responseData));

            MyCommon.printMessage(response.toString());

            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new UtilsResponse(Naming.department, Naming.delete, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }
}
