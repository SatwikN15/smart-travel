package com.travelmanager.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.travelmanager.database.Database;
import com.travelmanager.model.Schedule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleController {
    private final Gson gson = new Gson();

    public String getAllSchedules() throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT s.*, r.name as route_name, r.origin, r.destination " +
                     "FROM schedules s LEFT JOIN routes r ON s.route_id = r.id " +
                     "ORDER BY s.time ASC";
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
        }
        
        return gson.toJson(schedules);
    }

    public String createSchedule(String body) throws SQLException {
        Schedule schedule = gson.fromJson(body, Schedule.class);
        
        String sql = "INSERT INTO schedules (route_id, time, days) VALUES (?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, schedule.getRouteId());
            pstmt.setString(2, schedule.getTime());
            pstmt.setString(3, schedule.getDays());
            
            pstmt.executeUpdate();
            
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                long id = keys.getLong(1);
                return getScheduleById(id, conn);
            }
        }
        
        return "{\"error\":\"Failed to create schedule\"}";
    }

    public String deleteSchedule(long id) throws SQLException {
        String sql = "DELETE FROM schedules WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            int deleted = pstmt.executeUpdate();
            
            if (deleted > 0) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Schedule deleted successfully");
                response.addProperty("id", id);
                return gson.toJson(response);
            }
        }
        
        return "{\"error\":\"Schedule not found\"}";
    }

    private String getScheduleById(long id, Connection conn) throws SQLException {
        String sql = "SELECT s.*, r.name as route_name, r.origin, r.destination " +
                     "FROM schedules s LEFT JOIN routes r ON s.route_id = r.id " +
                     "WHERE s.id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return gson.toJson(mapResultSetToSchedule(rs));
            }
        }
        
        return "{\"error\":\"Schedule not found\"}";
    }

    private Schedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule(
            rs.getLong("id"),
            rs.getLong("route_id"),
            rs.getString("time"),
            rs.getString("days"),
            rs.getString("created_at")
        );
        
        schedule.setRouteName(rs.getString("route_name"));
        schedule.setOrigin(rs.getString("origin"));
        schedule.setDestination(rs.getString("destination"));
        
        return schedule;
    }
}