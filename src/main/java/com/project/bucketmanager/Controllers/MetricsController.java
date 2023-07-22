package com.project.bucketmanager.Controllers;

import com.project.bucketmanager.Models.AvailableMetricsResponse;
import com.project.bucketmanager.Services.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    private final MetricsService metricsService;
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    @GetMapping("/{metricName}")
    public ResponseEntity<?> getMetrics(
            @PathVariable String metricName,
            @RequestParam String bucketName,
            @RequestParam String typeOfStatistics
    ){
        Object result = metricsService.getMetrics(metricName,bucketName,typeOfStatistics);
        return ResponseEntity.ok(result);
    }
    @GetMapping
    public ResponseEntity<AvailableMetricsResponse> getAvaliableMetrics(){
        AvailableMetricsResponse result = metricsService.getAvaliableMetrics();
        return ResponseEntity.ok(result);
    }
}
