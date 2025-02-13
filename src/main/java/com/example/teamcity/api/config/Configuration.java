package com.example.teamcity.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final String CONFIG_PROPERTIES = "config.properties";
    private static Configuration configuration;
    private Properties properties;

    private Configuration() {
        properties = new Properties();
        loadProperties(CONFIG_PROPERTIES);
    }

    public static Configuration getConfig() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    private void loadProperties(String fileName) {
        try (InputStream stream = Configuration.class.getClassLoader().getResourceAsStream(fileName)) {
            if (stream == null) {
                System.err.println("Config file not found: " + fileName);
            }
            properties.load(stream);
        } catch (IOException e) {
            System.err.println("Error loading config file: " + fileName);
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        return getConfig().properties.getProperty(key);

    }
}
