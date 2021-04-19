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

    String createUsersTable = "CREATE TABLE IF NOT EXISTS " + Naming.USERS_TABLE_NAME + "(" +
            Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
            Naming.PHONE_NUMBER + " VARCHAR(255) NOT NULL, " +
            Naming.PASSWORD + " VARCHAR(255) NOT NULL, " +
            Naming.CREATED_AT + " DATETIME NOT NULL, " +
            Naming.UPDATED_AT + " TIMESTAMP NULL, " +
            Naming.UUID + " VARCHAR(255) NOT NULL, " +
            Naming.PARENT + " VARCHAR(255) NOT NULL, " +
            Naming.ROLE + " VARCHAR(255) NOT NULL, " +
            Naming.SIGNATURE + " VARCHAR(255) NOT NULL) ENGINE=INNODB;";

    String createProfilesTable = "CREATE TABLE IF NOT EXISTS " + Naming.PROFILES_TABLE_NAME + "(" +
            Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
            Naming.FULL_NAME + " VARCHAR(255) NOT NULL, " +
            Naming.BIRTH_DATE + " TIMESTAMP NULL, " +
            Naming.ID_CARD + " VARCHAR(255) NOT NULL, " +
            Naming.PASSPORT + " VARCHAR(255) NOT NULL, " +
            Naming.CREATED_AT + " DATETIME NOT NULL, " +
            Naming.UPDATED_AT + " TIMESTAMP NULL, " +
            Naming.UUID + " VARCHAR(255) NOT NULL) ENGINE=INNODB;";

    String createCompaniesTable = "CREATE TABLE IF NOT EXISTS " + Naming.COMPANIES_TABLE_NAME + "(" +
            Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
            Naming.COMPANY + " VARCHAR(255) NOT NULL, " +
            Naming.ADDRESS + " VARCHAR(255) NOT NULL, " +
            Naming.LAT + " VARCHAR(255) NOT NULL, " +
            Naming.LNG + " VARCHAR(255) NOT NULL, " +
            Naming.CREATED_AT + " DATETIME NOT NULL, " +
            Naming.UPDATED_AT + " TIMESTAMP NULL, " +
            Naming.UUID + " VARCHAR(255) NOT NULL) ENGINE=INNODB;";

    private String queryCreateProjectsTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.PROFILES_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.PROJECT_NAME + " VARCHAR(255) NOT NULL, " +
                Naming.PROJECT_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.DESCRIPTION + " VARCHAR(255) NOT NULL, " +
                Naming.CREATED_AT + " DATETIME NOT NULL, " +
                Naming.UPDATED_AT + " TIMESTAMP NULL, " +
                Naming.OWNER_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.PROGRESS + " FLOAT(11) NULL, " +
                Naming.ASSIGNED + " TEXT NULL, " +
                Naming.PROJECT_VALUE + " FLOAT(11) NULL, " +
                Naming.START_TIME + " DATETIME NULL, " +
                Naming.END_TIME + " DATETIME NULL, " +
                Naming.FINISHED_AT + " DATETIME NULL, " +
                Naming.ATTACHMENT + " TEXT NULL, " +
                Naming.LAT + " FLOAT(11) NULL, " +
                Naming.LNG + " FLOAT(11) NULL) ENGINE=INNODB";
    }

    private String queryCreateContractsTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.CONTRACTS_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.SENDER_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.RECEIVER_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.PROJECT_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.CREATED_AT + " DATETIME NOT NULL, " +
                Naming.UPDATED_AT + " TIMESTAMP NULL, " +
                Naming.SENDER_SIGNATURE + " VARCHAR(255) NULL, " +
                Naming.RECEIVER_SIGNATURE + " VARCHAR(255) NULL) ENGINE=INNODB";
    }

    private String queryCreateJobsTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.JOBS_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.JOB_NAME + " VARCHAR(255) NOT NULL, " +
                Naming.DESCRIPTION + " VARCHAR(255) NOT NULL, " +
                Naming.JOB_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.CREATED_AT + " DATETIME NOT NULL, " +
                Naming.UPDATED_AT + " TIMESTAMP NULL, " +
                Naming.PROJECT_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.OWNER + " VARCHAR(255) NOT NULL, " +
                Naming.IS_DONE + " BOOLEAN NULL, " +
                Naming.ASSIGNED + " TEXT NULL, " +
                Naming.START_TIME + " DATETIME NULL, " +
                Naming.END_TIME + " DATETIME NULL, " +
                Naming.ATTACHMENT + " TEXT NULL, " +
                Naming.LAT + " FLOAT(11) NULL, " +
                Naming.LNG + " FLOAT(11) NULL) ENGINE=INNODB";
    }

    private String queryCreateDailyTasksTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.DAILY_TASKS_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.TASK_NAME + " VARCHAR(255) NOT NULL, " +
                Naming.DESCRIPTION + " VARCHAR(255) NOT NULL, " +
                Naming.CREATED_AT + " DATETIME NULL, " +
                Naming.UPDATED_AT + " TIMESTAMP NULL, " +
                Naming.OWNER_UUID + " VARCHAR(255) NULL, " +
                Naming.PROGRESS + " FLOAT(11) NULL, " +
                Naming.START_TIME + " DATETIME NULL, " +
                Naming.END_TIME + " DATETIME NULL, " +
                Naming.LAT + " FLOAT(11) NULL, " +
                Naming.LNG + " FLOAT(11) NULL, " +
                Naming.UUID + " VARCHAR(255) NOT NULL) ENGINE=INNODB";
    }

    private String queryCreateTaskItemTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.TASKS_ITEM_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.TASK_NAME + " VARCHAR(255) NOT NULL, " +
                Naming.DESCRIPTION + " VARCHAR(255) NOT NULL, " +
                Naming.CREATED_AT + " DATETIME NULL, " +
                Naming.UPDATED_AT + " TIMESTAMP NULL, " +
                Naming.PROJECT_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.IS_DONE + " BOOLEAN NULL, " +
                Naming.START_TIME + " DATETIME NULL, " +
                Naming.END_TIME + " DATETIME NULL, " +
                Naming.ATTACHMENT + " TEXT NULL, " +
                Naming.LAT + " FLOAT(11) NULL, " +
                Naming.LNG + " FLOAT(11) NULL, " +
                Naming.UUID + " VARCHAR(255) NOT NULL) ENGINE=INNODB";
    }

    private String queryCreatePayRollTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.PAYROLLS_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.VALUE_PER_DAY + " FLOAT(11) NULL, " +
                Naming.DESCRIPTION + " VARCHAR(255) NULL) ENGINE=INNODB";
    }

    private String queryCreateEmploymentTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.EMPLOYMENTS_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.SALARY_UUID + " INT(11) NOT NULL, " +
                Naming.TO_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.APPROVED_BY_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.CREATED_AT + " DATETIME NULL, " +
                Naming.UPDATED_AT + " TIMESTAMP NULL, " +
                Naming.START_TIME + " DATETIME NULL, " +
                Naming.END_TIME + " DATETIME NULL) ENGINE=INNODB";
    }

    private String queryCreateLeaveTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.LEAVES_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.START_TIME + " TIMESTAMP NOT NULL, " +
                Naming.END_TIME + " TIMESTAMP NULL, " +
                Naming.DESCRIPTION + " VARCHAR(255) NULL, " +
                Naming.CREATED_AT + " DATETIME NULL, " +
                Naming.UPDATED_AT + " TIMESTAMP NULL, " +
                Naming.REASON + " VARCHAR(255) NULL, " +
                Naming.ATTACHMENT + " TEXT NULL, " +
                Naming.BY_UUID + " VARCHAR(255) NOT NULL, " +
                Naming.APPROVED_BY_UUID + " VARCHAR(255) NULL, " +
                Naming.APPROVED_TIME + " DATETIME NULL) ENGINE=INNODB";
    }

    private String queryCreateDepartmentTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.DEPARTMENTS_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.NAME + " VARCHAR(255) NOT NULL, " +
                Naming.PARENT + " VARCHAR(255) NOT NULL, " +
                Naming.REMARK + " VARCHAR(255) NULL, " +
//                Naming.MANAGER_UUID + " VARCHAR(255) NULL, " +
//                Naming.LAT + " FLOAT NULL, " +
//                Naming.LNG + " FLOAT NULL, " +
                Naming.CREATED_AT + " DATETIME NOT NULL, " +
                Naming.UPDATED_AT + " TIMESTAMP NULL, " +
                Naming.UUID + " VARCHAR(255) NOT NULL) ENGINE=INNODB";
    }

    private String queryCreateAttendanceTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.ATTENDANCE_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.START_TIME + " DATETIME NOT NULL, " +
                Naming.END_TIME + " DATETIME NULL, " +
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

    private String queryCreateSettingsTable(String uuid) {
        return "CREATE TABLE IF NOT EXISTS " + Naming.SETTINGS_TABLE_NAME + "_" + uuid + "(" +
                Naming.ID + " INT AUTO_INCREMENT PRIMARY KEY, " +
                Naming.START_WORKING + " DATETIME NOT NULL, " +
                Naming.END_WORKING + " DATETIME NOT NULL, " +
                Naming.OT_RATE + " FLOAT(11) NULL, " +
                Naming.VAT + " FLOAT(11) NULL) ENGINE=INNODB";
    }

    public void createTable(String uuid, Session session) {

        session.createSQLQuery(queryCreateProjectsTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreateContractsTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreateJobsTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreateDailyTasksTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreateTaskItemTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreatePayRollTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreateEmploymentTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreateLeaveTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreateSettingsTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreateAttendanceTable(uuid)).executeUpdate();
        session.createSQLQuery(queryCreateDepartmentTable(uuid)).executeUpdate();

    }

    public void initTable() {
        try (Session session = HibernateConnector.getInstance().getFactory().openSession()) {

            session.beginTransaction();

            session.createSQLQuery(createUsersTable).executeUpdate();
            session.createSQLQuery(createProfilesTable).executeUpdate();
            session.createSQLQuery(createCompaniesTable).executeUpdate();

            session.getTransaction().commit();

        } catch (Exception e) {
            MyCommon.printMessage("Couldn't create table");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
