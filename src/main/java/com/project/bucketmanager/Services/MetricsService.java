package com.project.bucketmanager.Services;

import com.project.bucketmanager.Models.AvailableMetricsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;

import java.util.List;

public interface MetricsService {
    List<Datapoint> getMetrics(String bucketName, String metricName, String typeOfStatistics);
    AvailableMetricsResponse getAvaliableMetrics();
}
