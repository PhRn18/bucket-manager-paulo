package com.project.bucketmanager.Services.Impl;

import com.project.bucketmanager.Services.MetricsService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

@Service
public class MetricsServiceImpl implements MetricsService {
    private final CloudWatchClient cloudWatchClient;
    public MetricsServiceImpl(CloudWatchClient cloudWatchClient) {
        this.cloudWatchClient = cloudWatchClient;
    }
    @Override
    public Object getMetrics() {
        return null;
    }

    @Override
    public Object getAvaliableMetrics() {
        return null;
    }
}
