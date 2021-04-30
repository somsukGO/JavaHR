package com.laoapps.database.connector;

import com.laoapps.database.entity.*;
import com.laoapps.utils.UtilConfig;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public class HibernateConnector {

    private HibernateConnector() {
    }

    private static HibernateConnector hibernateConnector = null;

    public static HibernateConnector getInstance() {
        if (hibernateConnector == null) {
            hibernateConnector = new HibernateConnector();
        }

        return hibernateConnector;
    }

    private final Properties properties = UtilConfig.getProperties();

    private final SessionFactory factory = getConfiguration().buildSessionFactory();

    public SessionFactory getFactory() {
        return factory;
    }

    private Configuration getConfiguration() {
        Configuration config = new Configuration();

        config.setProperty(Environment.DRIVER, properties.getProperty("database.driver_class"));
        config.setProperty(Environment.URL, properties.getProperty("database.url"));
        config.setProperty(Environment.USER, properties.getProperty("database.username"));
//        config.setProperty(Environment.PASS, properties.getProperty("database.password"));
        config.setProperty(Environment.DIALECT, properties.getProperty("database.dialect"));
        config.setProperty(Environment.SHOW_SQL, properties.getProperty("database.show_sql"));
        config.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, properties.getProperty("database.context_class"));
        config.setProperty(Environment.POOL_SIZE, properties.getProperty("database.pool_size"));

        config.addAnnotatedClass(Profiles.class);
        config.addAnnotatedClass(Department.class);
        config.addAnnotatedClass(Attendance.class);
        config.addAnnotatedClass(Companies.class);
        config.addAnnotatedClass(Personnel.class);
        config.addAnnotatedClass(Invite.class);

        return config;
    }

}
