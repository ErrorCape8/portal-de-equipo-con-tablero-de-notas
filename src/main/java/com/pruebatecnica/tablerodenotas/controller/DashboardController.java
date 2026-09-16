package com.pruebatecnica.tablerodenotas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pruebatecnica.tablerodenotas.service.DashboardMetricsService;
import com.pruebatecnica.tablerodenotas.service.DashboardMetricsService.DashboardMetrics;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardMetricsService dashboardMetricsService;

    public DashboardController(DashboardMetricsService dashboardMetricsService) {
        this.dashboardMetricsService = dashboardMetricsService;
    }

    @GetMapping("/metrics")
    public DashboardMetrics metrics() {
        return dashboardMetricsService.calculate();
    }
}