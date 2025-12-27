package com.travelmanager.model;

public class Statistics {
    private Long totalRoutes;
    private Double totalDistance;
    private Double totalCost;
    private Double totalTime;
    private Double carbonFootprint;

    public Statistics() {}

    public Statistics(Long totalRoutes, Double totalDistance, Double totalCost, 
                     Double totalTime, Double carbonFootprint) {
        this.totalRoutes = totalRoutes;
        this.totalDistance = totalDistance;
        this.totalCost = totalCost;
        this.totalTime = totalTime;
        this.carbonFootprint = carbonFootprint;
    }

    public Long getTotalRoutes() { return totalRoutes; }
    public void setTotalRoutes(Long totalRoutes) { this.totalRoutes = totalRoutes; }

    public Double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public Double getTotalTime() { return totalTime; }
    public void setTotalTime(Double totalTime) { this.totalTime = totalTime; }

    public Double getCarbonFootprint() { return carbonFootprint; }
    public void setCarbonFootprint(Double carbonFootprint) { this.carbonFootprint = carbonFootprint; }
}