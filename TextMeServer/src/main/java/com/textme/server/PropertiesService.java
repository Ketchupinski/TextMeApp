package com.textme.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {
    private static final Properties properties = new Properties();

    public synchronized static String getProperty(String property) {
        try(InputStream inputStream = new FileInputStream("config.properties")) {
            properties.load(inputStream);
            return properties.getProperty(property);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
