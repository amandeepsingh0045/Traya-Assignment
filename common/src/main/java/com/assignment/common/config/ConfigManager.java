package com.assignment.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads config/{env}.properties from the classpath. System properties override file values,
 * so anything can be changed from the command line: -Denv=qa -Dbrowser=firefox
 */
public final class ConfigManager {

    private static final Properties PROPS = new Properties();

    static {
        String env = System.getProperty("env", "qa");
        String file = "config/" + env + ".properties";
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new IllegalStateException("Config file not found on classpath: " + file);
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load " + file, e);
        }
    }

    private ConfigManager() { }

    public static String get(String key) {
        String value = System.getProperty(key, PROPS.getProperty(key));
        if (value == null) {
            throw new IllegalArgumentException("Missing config key: " + key);
        }
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        return System.getProperty(key, PROPS.getProperty(key, defaultValue)).trim();
    }

    public static int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }
}
