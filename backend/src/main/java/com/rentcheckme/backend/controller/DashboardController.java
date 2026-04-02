package com.rentcheckme.backend.controller;

import com.rentcheckme.backend.dto.DashboardRequest;
import com.rentcheckme.backend.dto.DashboardResponse;
import com.rentcheckme.backend.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse getDashboard(@RequestParam(required = false) String userId,
                                          @RequestParam(required = false) List<String> areaId) {
        return dashboardService.getDashboard(userId, areaId);
    }

    @PostMapping("/calculate")
    public DashboardResponse calculateDashboard(@RequestBody DashboardRequest request) {
        return dashboardService.buildDashboard(request);
    }
}
