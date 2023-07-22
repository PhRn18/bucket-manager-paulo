package com.project.bucketmanager.Models;

import java.util.Set;

public class AvailableMetrics {
    private Set<String> metrics;

    public AvailableMetrics(Set<String> metrics) {
        this.metrics = metrics;
    }

    public AvailableMetrics() {
    }

    public Set<String> getMetrics() {
        return metrics;
    }
    public boolean metricIsAvailable(String metricName){
        return metrics
                .stream()
                .anyMatch(metricName::equals);
    }
}
