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
    public Object getMetrics(String metricName,String bucketName, String typeOfStatistics) {

        Statistic statistic = Statistic.fromValue(typeOfStatistics);
        GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
                .namespace("AWS/S3")
                .metricName(metricName)
                .startTime(Instant.now().minus(7, ChronoUnit.DAYS))
                .endTime(Instant.now())
                .period(86400)
                .statistics(statistic)
                .dimensions(Dimension.builder().name("BucketName").value(bucketName).build())
                .build();
        GetMetricStatisticsResponse response = cloudWatchClient.getMetricStatistics(request);

        for (Datapoint datapoint : response.datapoints()) {
            System.out.println("Timestamp: " + datapoint.timestamp());
            System.out.println("Valor: " + datapoint.sum());
            System.out.println("------------------------------------");
        }
        return null;
    }

    @Override
    public AvailableMetricsResponse getAvaliableMetrics() {
        return new AvailableMetricsResponse(availableMetrics.getMetrics());
    }
}
