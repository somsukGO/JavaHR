package com.laoapps.handler;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Employees;
import com.laoapps.models.CheckJwtResult;
import com.laoapps.socket.response.Response;
import com.laoapps.socket.response.ResponseBody;
import com.laoapps.socket.response.ResponseData;
import com.laoapps.utils.MyCommon;
import com.laoapps.utils.Naming;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

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

    public String get(JsonObject data, CheckJwtResult checkJwtResult) {

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            int page = data.get(Naming.page).getAsInt();
            int limit = data.get(Naming.limit).getAsInt();

            ArrayList<Object> getKeyword = MyCommon.getKeyword(data);
            String keyword = (String) getKeyword.get(0);
            int keywordId = (int) getKeyword.get(1);

            session.beginTransaction();

            String searchQuery = "from Employees where id = " + keywordId +
                    " or firstName like '%" + keyword +
                    "%' or lastName like '%" + keyword +
                    "%' or phoneNumber like '%" + keyword +
                    "%' or email like '%" + keyword +
                    "%' or address like '%" + keyword +
                    "%' or birthDate like '%" + keyword +
                    "%' or idCard like '%" + keyword +
                    "%' or passport like '%" + keyword +
                    "%' or position like '%" + keyword +
                    "%' or role like '%" + keyword +
                    "%' or salary like '%" + keyword +
                    "%' or status like '%" + keyword +
                    "%' order by id desc";

            String query = (keyword == null) ? "from Employees order by id desc" : searchQuery;

            String countQuery = (keyword == null) ? "select count(id) from Employees" : "select count(id) " + searchQuery;
            Long count = (Long) session.createQuery(countQuery).uniqueResult();
            Integer totalPage = (count.intValue() + limit - 1) / limit;

            @SuppressWarnings("unchecked")
            ArrayList<Employees> employees = (ArrayList<Employees>) session.createQuery(query).setFirstResult((page - 1) * limit)
                    .setMaxResults(limit).getResultList();

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());
            responseData.setEmployees(employees);
            responseData.setTotalPage(totalPage);
            responseData.setTotalElement(count.intValue());
            responseData.setCurrentPage(page);
            responseData.setLimit(limit);

            Response response = new Response(new ResponseBody(Naming.employee, Naming.get, Naming.success,
                    "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.employee, Naming.get, Naming.fail,
                    e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

    public String update(JsonObject data, CheckJwtResult checkJwtResult) {

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            String employeeUuid = data.get(Naming.employeeUuid).getAsString();

            String firstName = null, lastName = null, phoneNumber = null, email = null, address = null, birthDate = null, idCard = null, passport = null, position =
                    null, role = null, departmentUuid = null, salary = null;

            if (data.has(Naming.firstName)) firstName = data.get(Naming.firstName).getAsString();
            if (data.has(Naming.lastName)) lastName = data.get(Naming.lastName).getAsString();
            if (data.has(Naming.phoneNumber)) phoneNumber = data.get(Naming.phoneNumber).getAsString();
            if (data.has(Naming.EMAIL)) email = data.get(Naming.EMAIL).getAsString();
            if (data.has(Naming.ADDRESS)) address = data.get(Naming.ADDRESS).getAsString();
            if (data.has(Naming.birthDate)) birthDate = data.get(Naming.birthDate).getAsString();
            if (data.has(Naming.idCard)) idCard = data.get(Naming.idCard).getAsString();
            if (data.has(Naming.PASSPORT)) passport = data.get(Naming.PASSPORT).getAsString();
            if (data.has(Naming.POSITION)) position = data.get(Naming.POSITION).getAsString();
            if (data.has(Naming.ROLE)) role = data.get(Naming.ROLE).getAsString();
            if (data.has(Naming.departmentUuid)) departmentUuid = data.get(Naming.departmentUuid).getAsString();
            if (data.has(Naming.SALARY)) salary = data.get(Naming.SALARY).getAsString();

            session.beginTransaction();

            String getRole = (String) session.createQuery("select role from Employees where userUuid = '" + checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();
            if (!getRole.equals(Naming.admin) && !getRole.equals(Naming.hr)) throw new RuntimeException("access deny");

            Employees employee = session.get(Employees.class, employeeUuid);
            if (employee == null) throw new RuntimeException("employee uuid not exists");

            if (!Strings.isNullOrEmpty(firstName)) employee.setFirstName(firstName);
            if (!Strings.isNullOrEmpty(lastName)) employee.setLastName(lastName);
            if (!Strings.isNullOrEmpty(phoneNumber)) employee.setPhoneNumber(phoneNumber);
            if (!Strings.isNullOrEmpty(email)) employee.setEmail(email);
            if (!Strings.isNullOrEmpty(address)) employee.setAddress(address);
            if (!Strings.isNullOrEmpty(birthDate)) employee.setBirthDate(birthDate);
            if (!Strings.isNullOrEmpty(idCard)) employee.setIdCard(idCard);
            if (!Strings.isNullOrEmpty(passport)) employee.setPassport(passport);
            if (!Strings.isNullOrEmpty(position)) employee.setPosition(position);
            if (!Strings.isNullOrEmpty(salary)) employee.setSalary(salary);
            if (!Strings.isNullOrEmpty(role)) {
                if (!MyCommon.roles().contains(role)) throw new RuntimeException("invalid role");
                if (role.equals(Naming.hr) || employee.getRole().equals(Naming.hr)) {
                    if (!getRole.equals(Naming.admin)) throw new RuntimeException("access deny");
                    employee.setRole(role);
                }
            }
            if (!Strings.isNullOrEmpty(departmentUuid)) {
                String getDepartment = (String) session.createQuery("select uuid from Department where uuid = '" + departmentUuid + "'").uniqueResult();
                if (getDepartment == null) throw new RuntimeException("department not exists");
                employee.setDepartmentUuid(departmentUuid);
            }

            session.update(employee);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());
            employee.calculateAge();
            responseData.setEmployee(employee);

            Response response = new Response(new ResponseBody(Naming.employee, Naming.update, Naming.success,
                    "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.employee, Naming.update, Naming.fail,
                    e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }

    public String delete(JsonObject data, CheckJwtResult checkJwtResult) {

        try (Session session = factory.withOptions().interceptor(new CustomInterceptor(checkJwtResult.getCheckJwt().getCompany())).openSession()) {

            String employeeUuid = data.get(Naming.employeeUuid).getAsString();

            session.beginTransaction();

            String getRole = (String) session.createQuery("select role from Employees where userUuid = '" + checkJwtResult.getCheckJwt().getUuid() + "'").uniqueResult();
            if (!getRole.equals(Naming.admin) && !getRole.equals(Naming.hr)) throw new RuntimeException("access deny");

            Employees employee = session.get(Employees.class, employeeUuid);
            if (employee == null) throw new RuntimeException("employee uuid not exists");

            if (employee.getRole().equals(Naming.admin)) throw new RuntimeException("access deny");
            if (employee.getRole().equals(Naming.hr)) if (!getRole.equals(Naming.admin)) throw new RuntimeException("access deny");
            employee.setStatus(Naming.deleted);

            session.update(employee);

            session.getTransaction().commit();

            ResponseData responseData = new ResponseData();
            responseData.setJwt(checkJwtResult.getCheckJwt().getJwt());
            employee.calculateAge();
            responseData.setEmployee(employee);

            Response response = new Response(new ResponseBody(Naming.employee, Naming.delete, Naming.success,
                    "successful", responseData));
            MyCommon.printMessage(response.toString());
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response(new ResponseBody(Naming.employee, Naming.delete, Naming.fail,
                    e.getMessage(), null));
            MyCommon.printMessage(response.toString());

            return gson.toJson(response);
        }

    }
}
