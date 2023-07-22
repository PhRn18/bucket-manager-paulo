package com.project.bucketmanager.Models;

import java.util.Set;

public class AvailableMetricsResponse{
    private Set<String> metrics;
    public AvailableMetricsResponse(Set<String> metrics) {
        this.metrics = metrics;
    }
    public AvailableMetricsResponse(AvailableMetrics availableMetrics){
        this.metrics = availableMetrics.getMetrics();
    }
    public Set<String> getMetrics() {
        return metrics;
    }
}
