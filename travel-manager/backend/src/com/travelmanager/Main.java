package com.travelmanager;

import com.travelmanager.database.Database;
import com.travelmanager.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        try {
            Database.initialize();
            
            HttpServer server = new HttpServer(8080);
            server.start();
            
            System.out.println("✅ Travel Manager API is running!");
            System.out.println("📊 API Base URL: http://localhost:8080/api");
            System.out.println("Press Ctrl+C to stop the server");
            
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}