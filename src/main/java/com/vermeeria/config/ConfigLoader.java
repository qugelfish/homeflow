package com.vermeeria.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads configuration properties from the local configuration file.
 * The file is expected at {@code config/local.properties}.
 *
 * @author Jette
 */
public class ConfigLoader {

    /**
     * Loads the application configuration from {@code config/local.properties}.
     *
     * @return a {@link Properties} object containing the loaded configuration values
     * @throws RuntimeException if the configuration file cannot be found or read
     */
    public static Properties load() {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream("config/local.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config/local.properties", e);
        }

        return properties;
    }
}
