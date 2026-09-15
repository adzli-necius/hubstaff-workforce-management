package com.posthub.hubstaff.dashboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.posthub.hubstaff.common.api.ApiResponse;
import com.posthub.hubstaff.dashboard.dto.DashboardResponse;
import com.posthub.hubstaff.dashboard.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "DASHBOARD_RETRIEVED",
                "Dashboard retrieved successfully",
                dashboardService.getDashboard(authentication.getName())));
    }
}