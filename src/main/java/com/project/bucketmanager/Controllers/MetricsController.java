package com.project.bucketmanager.Controllers;

import com.project.bucketmanager.Services.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/metrics")
public class MetricsController {
    private final MetricsService metricsService;
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    @GetMapping
    public ResponseEntity<?> getMetrics(){

        Object result = metricsService.getMetrics();
        return ResponseEntity.ok(result);
    }
    @GetMapping("/avaliableMetrics")
    public ResponseEntity<?> getAvaliableMetrics(){
        Object result = metricsService.getAvaliableMetrics();
        return ResponseEntity.ok(result);
    }

}
