package com.laoapps;

import com.laoapps.database.connector.CustomInterceptor;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.database.entity.Department;
import org.hibernate.Session;

import java.util.ArrayList;

public class Example {

    public static void main(String[] args) {
        try (Session session = HibernateConnector.getInstance().getFactory().withOptions().interceptor(new CustomInterceptor("c761157cc2")).openSession()) {

            String role = (String) session.createQuery("select role from Users where uuid = 'c761157cc2'").uniqueResult();
            System.out.println(role);

        }
    }
}
