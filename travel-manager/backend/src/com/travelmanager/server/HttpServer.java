package com.travelmanager.server;

import com.sun.net.httpserver.*;
import com.travelmanager.controller.RouteController;
import com.travelmanager.controller.ScheduleController;
import java.io.*;
import java.net.InetSocketAddress;

public class HttpServer {
    private final int port;
    private com.sun.net.httpserver.HttpServer server;
    private final RouteController routeController;
    private final ScheduleController scheduleController;

    public HttpServer(int port) {
        this.port = port;
        this.routeController = new RouteController();
        this.scheduleController = new ScheduleController();
    }

    public void start() throws IOException {
        server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/api/routes/statistics", this::handleStatistics);
        server.createContext("/api/routes", this::handleRoutes);
        server.createContext("/api/schedules", this::handleSchedules);
        
        server.setExecutor(null);
        server.start();
    }

    private void handleRoutes(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        try {
            String response = "";
            int statusCode = 200;

            if (method.equals("GET") && parts.length == 3) {
                response = routeController.getAllRoutes();
            } else if (method.equals("GET") && parts.length == 4) {
                long id = Long.parseLong(parts[3]);
                response = routeController.getRouteById(id);
            } else if (method.equals("POST")) {
                String body = readRequestBody(exchange);
                response = routeController.createRoute(body);
                statusCode = 201;
            } else if (method.equals("PUT") && parts.length == 4) {
                long id = Long.parseLong(parts[3]);
                String body = readRequestBody(exchange);
                response = routeController.updateRoute(id, body);
            } else if (method.equals("PATCH") && parts.length == 5 && "favorite".equals(parts[4])) {
                long id = Long.parseLong(parts[3]);
                response = routeController.toggleFavorite(id);
            } else if (method.equals("DELETE") && parts.length == 4) {
                long id = Long.parseLong(parts[3]);
                response = routeController.deleteRoute(id);
            } else {
                response = "{\"error\":\"Not found\"}";
                statusCode = 404;
            }

            sendResponse(exchange, statusCode, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleStatistics(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        try {
            String response = routeController.getStatistics();
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleSchedules(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        try {
            String response = "";
            int statusCode = 200;

            if (method.equals("GET") && parts.length == 3) {
                response = scheduleController.getAllSchedules();
            } else if (method.equals("POST")) {
                String body = readRequestBody(exchange);
                response = scheduleController.createSchedule(body);
                statusCode = 201;
            } else if (method.equals("DELETE") && parts.length == 4) {
                long id = Long.parseLong(parts[3]);
                response = scheduleController.deleteSchedule(id);
            } else {
                response = "{\"error\":\"Not found\"}";
                statusCode = 404;
            }

            sendResponse(exchange, statusCode, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}