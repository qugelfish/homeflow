package com.vermeeria.persistence;

import com.vermeeria.config.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages the database connection for the application.
 * The connection settings are loaded from the local properties file.
 *
 * @author Jette
 */
public class DatabaseManager {

    private final String url;
    private final String user;
    private final String password;

    /**
     * Creates a new DatabaseManager using values from {@code config/local.properties}.
     */
    public DatabaseManager() {
        Properties properties = ConfigLoader.load();
        this.url = properties.getProperty("db.url");
        this.user = properties.getProperty("db.user");
        this.password = properties.getProperty("db.password");
    }

    /**
     * Establishes and returns a connection to the configured database.
     *
     * @return a {@link Connection} to the PostgreSQL database
     * @throws SQLException if the connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
