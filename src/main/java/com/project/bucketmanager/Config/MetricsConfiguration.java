package com.project.bucketmanager.Config;

import com.project.bucketmanager.Models.AvailableMetrics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
@Configuration
public class MetricsConfiguration {
    private final Environment environment;
    public MetricsConfiguration(Environment environment){
        this.environment = environment;
    }
    @Bean
    @ConditionalOnProperty(value = "aws.enable.metrics", havingValue = "true")
    public AvailableMetrics loadAvailableMetrics(){
        String[] names = environment.getRequiredProperty("aws.metrics.names", String[].class);
        HashSet<String> metrics = new HashSet<>(Arrays.asList(names));
        return new AvailableMetrics(metrics);
    }
    @Bean
    @ConditionalOnProperty(value = "aws.enable.metrics", havingValue = "false", matchIfMissing = true)
    public AvailableMetrics buildEmptyAvailableMetrics(){
        return new AvailableMetrics(Collections.emptySet());
    }
}
