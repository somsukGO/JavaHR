package com.laoapps.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UtilConfig {

    public static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream input = UtilConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

}
