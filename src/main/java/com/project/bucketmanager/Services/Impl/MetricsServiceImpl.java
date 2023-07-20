package com.project.bucketmanager.Services.Impl;

import com.project.bucketmanager.Models.AvailableMetrics;
import com.project.bucketmanager.Models.AvailableMetricsResponse;
import com.project.bucketmanager.Services.MetricsService;
import com.project.bucketmanager.Aspects.Validation.Annotations.ValidateMetricsParams;
import com.project.bucketmanager.Aspects.Validation.Annotations.ValidateStringParams;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MetricsServiceImpl implements MetricsService {
    private final CloudWatchClient cloudWatchClient;
    private final AvailableMetrics availableMetrics;
    public MetricsServiceImpl(CloudWatchClient cloudWatchClient, AvailableMetrics availableMetrics) {
        this.cloudWatchClient = cloudWatchClient;
        this.availableMetrics = availableMetrics;
    }
    @Override
    @ValidateStringParams
    @ValidateMetricsParams
    public List<Datapoint> getMetrics(String metricName, String bucketName, String typeOfStatistics) {
        Statistic statistic = Statistic.fromValue(typeOfStatistics);

        Dimension dimension = Dimension
                .builder()
                .name("BucketName")
                .value(bucketName)
                .build();

        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
                .namespace("AWS/S3")
                .metricName(metricName)
                .startTime(Instant.now().minus(7, ChronoUnit.DAYS))
                .endTime(Instant.now())
                .period(86400)
                .statistics(statistic)
                .dimensions(dimension)
                .build();

        return cloudWatchClient.getMetricStatistics(request).datapoints();
    }

    @Override
    public AvailableMetricsResponse getAvaliableMetrics() {
        return new AvailableMetricsResponse(availableMetrics.getMetrics());
    }
}
