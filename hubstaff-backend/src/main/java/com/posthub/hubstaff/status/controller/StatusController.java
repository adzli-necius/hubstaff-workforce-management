package com.posthub.hubstaff.status.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.posthub.hubstaff.common.api.ApiResponse;

/**
 * Provides a lightweight endpoint for confirming that the API is reachable.
 */
@RestController
@RequestMapping("/api/status")
public class StatusController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> getStatus() {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "SERVICE_UP",
                "Hubstaff backend is running",
                Map.of("status", "UP")));
    }
}
