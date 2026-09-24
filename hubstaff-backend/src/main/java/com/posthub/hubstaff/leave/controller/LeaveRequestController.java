package com.posthub.hubstaff.leave.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.posthub.hubstaff.common.api.ApiResponse;
import com.posthub.hubstaff.leave.dto.request.LeaveRequestCreateRequestDto;
import com.posthub.hubstaff.leave.dto.response.LeaveRequestResponseDto;
import com.posthub.hubstaff.leave.dto.response.LeaveRequestSummaryResponseDto;
import com.posthub.hubstaff.leave.service.LeaveRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<LeaveRequestResponseDto>> createLeaveRequest(
            Authentication authentication,
            @Valid @RequestBody LeaveRequestCreateRequestDto request) {

        LeaveRequestResponseDto leaveRequest =
                leaveRequestService.createLeaveRequestForUser(authentication.getName(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(),
                "LEAVE_REQUEST_CREATED",
                "Leave request created successfully",
                leaveRequest));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaveRequestResponseDto>>> getAllLeaveRequests() {

        List<LeaveRequestResponseDto> leaveRequests =
                leaveRequestService.getAllLeaveRequests();

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "LEAVE_REQUESTS_RETRIEVED",
                "Leave requests retrieved successfully",
                leaveRequests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequestResponseDto>> getLeaveRequestById(
            @PathVariable Long id) {

        LeaveRequestResponseDto leaveRequest =
                leaveRequestService.getLeaveRequestById(id);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "LEAVE_REQUEST_RETRIEVED",
                "Leave request retrieved successfully",
                leaveRequest));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<LeaveRequestSummaryResponseDto>> getLeaveRequestSummary() {

        LeaveRequestSummaryResponseDto summary =
                leaveRequestService.getLeaveRequestSummary();

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "LEAVE_REQUEST_SUMMARY_RETRIEVED",
                "Leave request summary retrieved successfully",
                summary));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LeaveRequestResponseDto>>> getLeaveRequests(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {

        List<LeaveRequestResponseDto> leaveRequests =
                leaveRequestService.getLeaveRequests(search, status);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "LEAVE_REQUESTS_RETRIEVED",
                "Leave requests retrieved successfully",
                leaveRequests));
    }

    
}
