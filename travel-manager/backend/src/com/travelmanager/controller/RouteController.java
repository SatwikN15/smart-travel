package com.travelmanager.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.travelmanager.database.Database;
import com.travelmanager.model.Route;
import com.travelmanager.model.Statistics;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RouteController {
    private final Gson gson = new Gson();

    public String getAllRoutes() throws SQLException {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM routes ORDER BY created_at DESC";
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                routes.add(mapResultSetToRoute(rs));
            }
        }
        
        return gson.toJson(routes);
    }

    public String getRouteById(long id) throws SQLException {
        String sql = "SELECT * FROM routes WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return gson.toJson(mapResultSetToRoute(rs));
            }
        }
        
        return "{\"error\":\"Route not found\"}";
    }

    public String createRoute(String body) throws SQLException {
        Route route = gson.fromJson(body, Route.class);
        
        String sql = "INSERT INTO routes (name, origin, destination, mode, distance, duration, cost) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, route.getName());
            pstmt.setString(2, route.getOrigin());
            pstmt.setString(3, route.getDestination());
            pstmt.setString(4, route.getMode());
            pstmt.setDouble(5, route.getDistance());
            pstmt.setInt(6, route.getDuration());
            pstmt.setDouble(7, route.getCost());
            
            pstmt.executeUpdate();
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                long id = keys.getLong(1);
                return getRouteById(id);
            }
        }
        
        return "{\"error\":\"Failed to create route\"}";
    }

    public String updateRoute(long id, String body) throws SQLException {
        Route route = gson.fromJson(body, Route.class);
        
        String sql = "UPDATE routes SET name = ?, origin = ?, destination = ?, mode = ?, " +
                     "distance = ?, duration = ?, cost = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, route.getName());
            pstmt.setString(2, route.getOrigin());
            pstmt.setString(3, route.getDestination());
            pstmt.setString(4, route.getMode());
            pstmt.setDouble(5, route.getDistance());
            pstmt.setInt(6, route.getDuration());
            pstmt.setDouble(7, route.getCost());
            pstmt.setLong(8, id);
            
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                return getRouteById(id);
            }
        }
        
        return "{\"error\":\"Route not found\"}";
    }

    public String toggleFavorite(long id) throws SQLException {
        String sql = "UPDATE routes SET favorite = NOT favorite, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int updated = pstmt.executeUpdate();
            
            if (updated > 0) {
                return getRouteById(id);
            }
        }
        
        return "{\"error\":\"Route not found\"}";
    }

    public String deleteRoute(long id) throws SQLException {
        String sql = "DELETE FROM routes WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int deleted = pstmt.executeUpdate();
            
            if (deleted > 0) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Route deleted successfully");
                response.addProperty("id", id);
                return gson.toJson(response);
            }
        }
        
        return "{\"error\":\"Route not found\"}";
    }

    public String getStatistics() throws SQLException {
        String sql = "SELECT COUNT(*) as total_routes, " +
                     "COALESCE(SUM(distance), 0) as total_distance, " +
                     "COALESCE(SUM(cost), 0) as total_cost, " +
                     "COALESCE(SUM(duration), 0) as total_time FROM routes";
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                long totalRoutes = rs.getLong("total_routes");
                double totalDistance = rs.getDouble("total_distance");
                double totalCost = rs.getDouble("total_cost");
                double totalTime = rs.getDouble("total_time") / 60.0;
                
                double carbonFootprint = calculateCarbonFootprint(conn);
                
                Statistics stats = new Statistics(
                    totalRoutes,
                    Math.round(totalDistance * 10.0) / 10.0,
                    Math.round(totalCost * 100.0) / 100.0,
                    Math.round(totalTime * 10.0) / 10.0,
                    Math.round(carbonFootprint * 10.0) / 10.0
                );
                
                return gson.toJson(stats);
            }
        }
        
        return "{}";
    }

    private double calculateCarbonFootprint(Connection conn) throws SQLException {
        String sql = "SELECT distance, mode FROM routes";
        double carbon = 0.0;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                double distance = rs.getDouble("distance");
                String mode = rs.getString("mode");
                double factor = switch (mode) {
                    case "car" -> 0.171;
                    case "bus" -> 0.089;
                    case "train" -> 0.041;
                    default -> 0.0;
                };
                carbon += distance * factor * 22 * 2;
            }
        }
        
        return carbon;
    }

    private Route mapResultSetToRoute(ResultSet rs) throws SQLException {
        return new Route(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("origin"),
            rs.getString("destination"),
            rs.getString("mode"),
            rs.getDouble("distance"),
            rs.getInt("duration"),
            rs.getDouble("cost"),
            rs.getInt("favorite") == 1,
            rs.getString("created_at"),
            rs.getString("updated_at")
        );
    }
}