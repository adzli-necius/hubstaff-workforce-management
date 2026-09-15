package com.posthub.hubstaff.attendance.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.posthub.hubstaff.attendance.dto.request.AttendanceClockRequest;
import com.posthub.hubstaff.attendance.dto.response.AttendanceResponseDto;
import com.posthub.hubstaff.attendance.service.AttendanceService;
import com.posthub.hubstaff.common.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

        @PostMapping("/me/clock-in")
        public ResponseEntity<ApiResponse<AttendanceResponseDto>> clockInForUser(
                    Authentication authentication,
                    @Valid @RequestBody AttendanceClockRequest request) {
                AttendanceResponseDto attendance = attendanceService.clockInForUser(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
            HttpStatus.CREATED.value(), "CLOCKED_IN", "Employee clocked in successfully", attendance));
        }

        @PostMapping("/me/clock-out")
        public ResponseEntity<ApiResponse<AttendanceResponseDto>> clockOutForUser(
                    Authentication authentication,
                    @Valid @RequestBody AttendanceClockRequest request) {
                AttendanceResponseDto attendance = attendanceService.clockOutForUser(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success(
            HttpStatus.OK.value(), "CLOCKED_OUT", "Employee clocked out successfully", attendance));
        }

        @GetMapping("/me/today")
        public ResponseEntity<ApiResponse<AttendanceResponseDto>> getTodayForUser(
                Authentication authentication) {
            AttendanceResponseDto attendance = attendanceService.getTodayForUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(
            HttpStatus.OK.value(), "ATTENDANCE_RETRIEVED", "Today's attendance retrieved successfully", attendance));
        }

        @GetMapping("/me/history")
        public ResponseEntity<ApiResponse<java.util.List<AttendanceResponseDto>>> getHistoryForUser(
                Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
            HttpStatus.OK.value(), "ATTENDANCE_HISTORY_RETRIEVED", "Attendance history retrieved successfully",
                attendanceService.getHistoryForUser(authentication.getName())));
        }

    @PostMapping("/clock-in")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> clockIn(
            @Valid @RequestBody AttendanceClockRequest request) {
        AttendanceResponseDto attendance = attendanceService.clockIn(request.employeeId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), "CLOCKED_IN", "Employee clocked in successfully", attendance));
    }

    @PostMapping("/clock-out")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> clockOut(
            @Valid @RequestBody AttendanceClockRequest request) {
        AttendanceResponseDto attendance = attendanceService.clockOut(request.employeeId(), request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "CLOCKED_OUT", "Employee clocked out successfully", attendance));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> getToday(@RequestParam Long employeeId) {
        AttendanceResponseDto attendance = attendanceService.getToday(employeeId);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "ATTENDANCE_RETRIEVED", "Today's attendance retrieved successfully", attendance));
    }
}
