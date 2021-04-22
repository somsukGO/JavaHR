package com.laoapps.handler;

import com.laoapps.utils.MyCommon;
import com.laoapps.database.connector.HibernateConnector;
import com.laoapps.utils.Naming;
import org.hibernate.Session;

public class TableHandler {
    private TableHandler() {
    }

    private static TableHandler tableHandler = null;

    public static TableHandler getInstance() {
        if (tableHandler == null) {
            tableHandler = new TableHandler();
        }

        return tableHandler;
    }

    String createProfilesTable = "CREATE TABLE IF NOT EXISTS " + Naming.PROFILES_TABLE_NAME + "(" +
            Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
            Naming.FIRST_NAME + " VARCHAR(255) NOT NULL, " +
            Naming.LAST_NAME + " VARCHAR(255) NOT NULL, " +
            Naming.PHONE_NUMBER + " VARCHAR(255) NULL, " +
            Naming.EMAIL + " VARCHAR(255) NULL, " +
            Naming.ADDRESS + " VARCHAR(255) NULL, " +
            Naming.BIRTH_DATE + " DATE NULL, " +
            Naming.ID_CARD + " VARCHAR(255) NULL, " +
            Naming.PASSPORT + " VARCHAR(255) NULL, " +
            Naming.CREATED_AT + " DATETIME NOT NULL, " +
            Naming.UPDATED_AT + " TIMESTAMP NULL, " +
            Naming.UUID + " VARCHAR(255) NOT NULL) ENGINE=INNODB;";

    String createInviteTable = "CREATE TABLE IF NOT EXISTS " + Naming.INVITE + "(" +
            Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
            Naming.POSITION + " VARCHAR(255) NOT NULL, " +
            Naming.DESCRIPTION + " VARCHAR(255) NULL, " +
            Naming.ROLE + " VARCHAR(255) NULL, " +
            Naming.SALARY + " VARCHAR(255) NULL, " +
            Naming.COMPANY_UUID + " VARCHAR(255) NOT NULL, " +
            Naming.TO_UUID + " VARCHAR(255) NOT NULL, " +
            Naming.CREATED_AT + " DATETIME NOT NULL, " +
            Naming.UPDATED_AT + " TIMESTAMP NULL) ENGINE=INNODB;";

    String createCompaniesTable = "CREATE TABLE IF NOT EXISTS " + Naming.COMPANIES_TABLE_NAME + "(" +
            Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
            Naming.NAME + " VARCHAR(255) NOT NULL, " +
            Naming.DESCRIPTION + " VARCHAR(255) NULL, " +
            Naming.PHONE_NUMBER + " VARCHAR(255) NULL, " +
            Naming.EMAIL + " VARCHAR(255) NULL, " +
            Naming.FAX + " VARCHAR(255) NULL, " +
            Naming.OWNER_UUID + " VARCHAR(255) NOT NULL, " +
            Naming.ADDRESS + " VARCHAR(255) NULL, " +
            Naming.LAT + " FLOAT(15) NULL, " +
            Naming.LNG + " FLOAT(15) NULL, " +
            Naming.ALT + " FLOAT(15) NULL, " +
            Naming.CREATED_AT + " DATETIME NOT NULL, " +
            Naming.UPDATED_AT + " TIMESTAMP NULL, " +
            Naming.UUID + " VARCHAR(255) NOT NULL) ENGINE=INNODB;";

    private String queryCreateDepartmentTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.DEPARTMENTS_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.NAME + " VARCHAR(255) NOT NULL, " +
                Naming.PARENT + " VARCHAR(255) NOT NULL, " +
                Naming.REMARK + " VARCHAR(255) NULL, " +
                Naming.CREATED_AT + " DATETIME NOT NULL, " +
                Naming.UPDATED_AT + " TIMESTAMP NULL, " +
                Naming.UUID + " VARCHAR(255) NOT NULL) ENGINE=INNODB";
    }

    private String queryCreateAttendanceTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.ATTENDANCE_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.DATE + " DATE NOT NULL, " +
                Naming.START_TIME + " TIME NOT NULL, " +
                Naming.END_TIME + " TIME NULL, " +
                Naming.START_DESCRIPTION + " VARCHAR(255) NULL, " +
                Naming.START_REASON + " VARCHAR(255) NULL, " +
                Naming.START_ATTACHMENT + " VARCHAR(255) NULL, " +
                Naming.END_DESCRIPTION + " VARCHAR(255) NULL, " +
                Naming.END_REASON + " VARCHAR(255) NULL, " +
                Naming.END_ATTACHMENT + " VARCHAR(255) NULL, " +
                Naming.BY_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.APPROVED_BY_UUID + " VARCHAR(255) NULL, " +
                Naming.APPROVED_TIME + " DATETIME NULL) ENGINE=INNODB";
    }

    String createPersonnelTable = "CREATE TABLE IF NOT EXISTS " + Naming.PERSONNEL_TABLE_NAME + "(" +
            Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
            Naming.FIRST_NAME + " VARCHAR(255) NOT NULL, " +
            Naming.LAST_NAME + " VARCHAR(255) NOT NULL, " +
            Naming.PHONE_NUMBER + " VARCHAR(255) NULL, " +
            Naming.EMAIL + " VARCHAR(255) NULL, " +
            Naming.ADDRESS + " VARCHAR(255) NULL, " +
            Naming.BIRTH_DATE + " DATE NULL, " +
            Naming.ID_CARD + " VARCHAR(255) NULL, " +
            Naming.PASSPORT + " VARCHAR(255) NULL, " +
            Naming.CREATED_AT + " DATETIME NOT NULL, " +
            Naming.UPDATED_AT + " TIMESTAMP NULL, " +
            Naming.POSITION + " VARCHAR(255) NOT NULL, " +
            Naming.ROLE + " VARCHAR(255) NOT NULL, " +
            Naming.DEPARTMENT_UUID + " VARCHAR(255) NOT NULL, " +
            Naming.UUID + " VARCHAR(255) NOT NULL) ENGINE=INNODB;";

    public void initTable() {
        try (Session session = HibernateConnector.getInstance().getFactory().openSession()) {

            session.beginTransaction();

            session.createSQLQuery(createProfilesTable).executeUpdate();
            session.createSQLQuery(createCompaniesTable).executeUpdate();
            session.createSQLQuery(createInviteTable).executeUpdate();

            session.getTransaction().commit();

        } catch (Exception e) {
            MyCommon.printMessage("Couldn't create table");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
