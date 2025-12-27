package com.travelmanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:travel_manager.db";
    private static Connection connection;

    // Initialize database
    public static void initialize() {
        try {
            // 🔴 REQUIRED: Explicitly load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(DB_URL);
            createTables();

            System.out.println("✅ Database initialized successfully");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC Driver not found");
            e.printStackTrace();

        } catch (SQLException e) {
            System.err.println("❌ Database initialization failed");
            e.printStackTrace();
        }
    }

    // Get connection safely
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    // Create tables
    private static void createTables() throws SQLException {

        try (Statement stmt = connection.createStatement()) {

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS routes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "origin TEXT NOT NULL, " +
                            "destination TEXT NOT NULL, " +
                            "mode TEXT NOT NULL CHECK(mode IN ('car','bus','train','bike','walk')), " +
                            "distance REAL NOT NULL CHECK(distance > 0), " +
                            "duration INTEGER NOT NULL CHECK(duration > 0), " +
                            "cost REAL NOT NULL CHECK(cost >= 0), " +
                            "favorite INTEGER DEFAULT 0 CHECK(favorite IN (0,1)), " +
                            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );

            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS schedules (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "route_id INTEGER NOT NULL, " +
                            "time TEXT NOT NULL, " +
                            "days TEXT NOT NULL CHECK(days IN ('weekdays','daily','weekends')), " +
                            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE" +
                            ")"
            );
        }
    }
}
