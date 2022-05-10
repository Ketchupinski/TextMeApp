package com.textme.client.service;

import com.textme.client.Client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Connector {
    private static final Client client;

    private static final Properties properties;

    static { // todo: reconnection
        try {
            client = new Client();
            properties = new Properties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Client getClient() {
        return client;
    }

    public static String getProperty(String property) {
        try(InputStream inputStream = new FileInputStream("config.properties")) {
            properties.load(inputStream);
            return properties.getProperty(property);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
