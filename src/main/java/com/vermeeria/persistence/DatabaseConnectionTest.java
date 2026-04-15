package com.vermeeria.persistence;

import java.sql.Connection;

public class DatabaseConnectionTest {
    static void main(String[] args) {
        try {
            DatabaseManager databaseManager = new DatabaseManager();
            Connection connection = databaseManager.getConnection();
            System.out.println("Connection successful: " + !connection.isClosed());
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
