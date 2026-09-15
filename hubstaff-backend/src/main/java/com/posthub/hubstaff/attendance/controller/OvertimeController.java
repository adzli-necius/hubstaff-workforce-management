package com.posthub.hubstaff.attendance.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.posthub.hubstaff.attendance.dto.request.OvertimeDecisionRequest;
import com.posthub.hubstaff.attendance.dto.request.OvertimeStartRequest;
import com.posthub.hubstaff.attendance.dto.response.OvertimeResponseDto;
import com.posthub.hubstaff.attendance.service.OvertimeService;
import com.posthub.hubstaff.common.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;

    @PostMapping("/attendance/me/overtime/start")
    public ResponseEntity<ApiResponse<OvertimeResponseDto>> start(
            Authentication authentication,
            @Valid @RequestBody(required = false) OvertimeStartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), "OVERTIME_STARTED", "Overtime started successfully",
                overtimeService.startForUser(authentication.getName(), request)));
    }

    @PostMapping("/attendance/me/overtime/end")
    public ResponseEntity<ApiResponse<OvertimeResponseDto>> end(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "OVERTIME_ENDED", "Overtime ended successfully",
                overtimeService.endForUser(authentication.getName())));
    }

    @GetMapping("/attendance/me/overtime")
    public ResponseEntity<ApiResponse<List<OvertimeResponseDto>>> history(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "OVERTIME_RETRIEVED", "Overtime history retrieved successfully",
                overtimeService.getHistoryForUser(authentication.getName())));
    }

    @GetMapping("/overtime/pending")
    public ResponseEntity<ApiResponse<List<OvertimeResponseDto>>> pending() {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "OVERTIME_PENDING_RETRIEVED", "Pending overtime retrieved successfully",
                overtimeService.getPending()));
    }

    @PatchMapping("/overtime/{id}/approve")
    public ResponseEntity<ApiResponse<OvertimeResponseDto>> approve(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody OvertimeDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "OVERTIME_APPROVED", "Overtime approved successfully",
                overtimeService.approve(id, authentication.getName(), request)));
    }

    @PatchMapping("/overtime/{id}/reject")
    public ResponseEntity<ApiResponse<OvertimeResponseDto>> reject(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody OvertimeDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "OVERTIME_REJECTED", "Overtime rejected successfully",
                overtimeService.reject(id, authentication.getName(), request)));
    }
}