package org.example.javafxdemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {

    private static final String DB_URL = "jdbc:sqlite:users.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    public static void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "encrypted_password TEXT NOT NULL," +
                "public_key BLOB," +
                "private_key BLOB);";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'users' is ready.");
        } catch (SQLException e) {
            System.out.println("Failed to create 'users' table: " + e.getMessage());
        }
    }

    public static void createUserDataTable(String username) {
        String tableName = "data_" + username;
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "login TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "last_modified TEXT NOT NULL);";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table '" + tableName + "' is ready for user '" + username + "'.");
        } catch (SQLException e) {
            System.out.println("Failed to create table for user '" + username + "': " + e.getMessage());
        }
    }
}
