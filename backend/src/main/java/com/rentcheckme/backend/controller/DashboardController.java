package com.rentcheckme.backend.controller;

import com.rentcheckme.backend.dto.DashboardRequest;
import com.rentcheckme.backend.dto.DashboardResponse;
import com.rentcheckme.backend.model.User;
import com.rentcheckme.backend.service.AuthService;
import com.rentcheckme.backend.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
    private final AuthService authService;

    public DashboardController(DashboardService dashboardService, AuthService authService) {
        this.dashboardService = dashboardService;
        this.authService = authService;
    }

    @GetMapping
    public DashboardResponse getDashboard(@RequestParam(required = false) String userId,
                                          @RequestHeader("Authorization") String authorization,
                                          @RequestParam(required = false) List<String> areaId) {
        User user = authService.requireUser(authorization, userId);
        return dashboardService.getDashboard(user.getId(), areaId);
    }

    @PostMapping("/calculate")
    public DashboardResponse calculateDashboard(@RequestHeader("Authorization") String authorization,
                                                @RequestBody DashboardRequest request) {
        User user = authService.requireUser(authorization, request.getUserId());
        request.setUserId(user.getId());
        return dashboardService.buildDashboard(request);
    }
}
