package com.travelmanager.model;

public class Route {
    private Long id;
    private String name;
    private String origin;
    private String destination;
    private String mode;
    private Double distance;
    private Integer duration;
    private Double cost;
    private Boolean favorite;
    private String createdAt;
    private String updatedAt;

    public Route() {
        this.favorite = false;
    }

    public Route(Long id, String name, String origin, String destination, String mode,
                 Double distance, Integer duration, Double cost, Boolean favorite,
                 String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.mode = mode;
        this.distance = distance;
        this.duration = duration;
        this.cost = cost;
        this.favorite = favorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public Boolean getFavorite() { return favorite; }
    public void setFavorite(Boolean favorite) { this.favorite = favorite; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public double calculateCarbonEmission() {
        double emissionFactor = switch (mode) {
            case "car" -> 0.171;
            case "bus" -> 0.089;
            case "train" -> 0.041;
            case "bike", "walk" -> 0.0;
            default -> 0.0;
        };
        return distance * emissionFactor;
    }
}