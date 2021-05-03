package com.laoapps.handler;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Department;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseData;
import com.laoapps.socket.response.ResponseBody;
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

    public String create(JsonObject data, CheckJwtResult checkJwtResult) {

        String name = data.get(Naming.NAME).getAsString();
        String parent = null, remark = null;

        if (data.has(Naming.PARENT)) parent = data.get(Naming.PARENT).getAsString();
        if (data.has(Naming.REMARK)) remark = data.get(Naming.REMARK).getAsString();

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            session.beginTransaction();

            String role = (String) session.createQuery("select role from Personnel where userUuid = '" + checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();

            if (!role.equals(Naming.admin) && !role.equals(Naming.hr)) {
                Response response = new Response(new ResponseBody(Naming.department, Naming.create, Naming.fail, "access denied", null));
                MyCommon.printMessage(response.toString());

                return gson.toJson(response);
            }

            Department department = new Department();

            department.setName(name);
            if (!Strings.isNullOrEmpty(parent)) department.setParent(parent);
            else department.setParent(Naming.self);
            if (!Strings.isNullOrEmpty(remark)) department.setRemark(remark);
            department.setCreatedAt(MyCommon.currentTime());
            department.setUuid(MyCommon.generateUuid());

            session.save(department);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setDepartment(department);
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new ResponseBody(Naming.department, Naming.create, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.department, Naming.create, Naming.fail,
                    e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

    public String getById(JsonObject data, CheckJwtResult checkJwtResult) {
        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            int id = data.get(Naming.ID).getAsInt();

            session.beginTransaction();

            Department department = (Department) session.createQuery("from Department where id = " + id + "").uniqueResult();

            if (department == null) throw new NullPointerException("Department id: " + id + " not exists");

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setDepartment(department);
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new ResponseBody(Naming.department, Naming.getById, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.department, Naming.getById, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }
    }

    public String get(JsonObject data, CheckJwtResult checkJwtResult) {

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            int page = data.get(Naming.page).getAsInt();
            int limit = data.get(Naming.limit).getAsInt();

            ArrayList<Object> getKeyword = MyCommon.getKeyword(data);
            String keyword = (String) getKeyword.get(0);
            int keywordId = (int) getKeyword.get(1);

            session.beginTransaction();

            String searchQuery = "from Department where id = " + keywordId + " or name like '%" + keyword +
                    "%' or parent like '%" + keyword + "%' or remark like '%" + keyword + "%' or createdAt like '%" + keyword +
                    "%' or updatedAt like '%" + keyword + "%' or uuid like '%" + keyword + "%' order by id desc";

            String query = (keyword == null) ? "from Department order by id desc" : searchQuery;

            String countQuery = (keyword == null) ? "select count(id) from Department" : "select count(id) " + searchQuery;
            Long count = (Long) session.createQuery(countQuery).uniqueResult();
            Integer totalPage = (count.intValue() + limit - 1) / limit;

            @SuppressWarnings("unchecked")
            ArrayList<Department> departments = (ArrayList<Department>) session.createQuery(query).setFirstResult((page - 1) * limit)
                    .setMaxResults(limit).getResultList();

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setDepartments(departments);
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());
            responseData.setTotalPage(totalPage);
            responseData.setTotalElement(count.intValue());
            responseData.setCurrentPage(page);
            responseData.setLimit(limit);

            Response response = new Response(new ResponseBody(Naming.department, Naming.get, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.department, Naming.get, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

    public String update(JsonObject data, CheckJwtResult checkJwtResult) {

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            String name = null, parent = null, remark = null;

            if (data.has(Naming.NAME)) name = data.get(Naming.NAME).getAsString();
            if (data.has(Naming.PARENT)) parent = data.get(Naming.PARENT).getAsString();
            if (data.has(Naming.REMARK)) remark = data.get(Naming.REMARK).getAsString();

            session.beginTransaction();

            String role = (String) session.createQuery("select role from Personnel where userUuid = '" + checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();

            if (!role.equals(Naming.admin) && !role.equals(Naming.hr)) {
                Response response = new Response(new ResponseBody(Naming.department, Naming.update, Naming.fail, "access denied", null));
                MyCommon.printMessage(response.toString());

                return gson.toJson(response);
            }

            Department department = session.get(Department.class, data.get(Naming.UUID).getAsString());

            if (!Strings.isNullOrEmpty(name)) department.setName(name);
            if (!Strings.isNullOrEmpty(parent)) department.setParent(parent);
            else department.setParent(Naming.self);
            if (!Strings.isNullOrEmpty(remark)) department.setRemark(remark);
            department.setUpdatedAt(MyCommon.currentTime());

            session.update(department);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setDepartment(department);
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new ResponseBody(Naming.department, Naming.update, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.department, Naming.update, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

    public String delete(JsonObject data, CheckJwtResult checkJwtResult) {


        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            String uuid = data.get(Naming.UUID).getAsString();
            session.beginTransaction();

            String role = (String) session.createQuery("select role from Personnel where userUuid = '" + checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();

            if (!role.equals(Naming.admin) && !role.equals(Naming.hr)) {
                Response response = new Response(new ResponseBody(Naming.department, Naming.delete, Naming.fail, "Access denied", null));
                MyCommon.printMessage(response.toString());

                return gson.toJson(response);
            }

            Department department = session.get(Department.class, uuid);

            if (department == null) throw new NullPointerException("Department uuid: " + uuid + " not exists");

            session.delete(department);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());

            Response response = new Response(new ResponseBody(Naming.department, Naming.delete, Naming.success, "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.department, Naming.delete, Naming.fail, e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }
}
