package com.travelmanager.model;

public class Schedule {
    private Long id;
    private Long routeId;
    private String time;
    private String days;
    private String routeName;
    private String origin;
    private String destination;
    private String createdAt;

    public Schedule() {}

    public Schedule(Long id, Long routeId, String time, String days, String createdAt) {
        this.id = id;
        this.routeId = routeId;
        this.time = time;
        this.days = days;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDays() { return days; }
    public void setDays(String days) { this.days = days; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}