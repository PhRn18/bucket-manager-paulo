package com.project.bucketmanager.Services;

import com.project.bucketmanager.Models.AvailableMetricsResponse;

public interface MetricsService {
    Object getMetrics(String bucketName,String metricName,String typeOfStatistics);
    AvailableMetricsResponse getAvaliableMetrics();
}
