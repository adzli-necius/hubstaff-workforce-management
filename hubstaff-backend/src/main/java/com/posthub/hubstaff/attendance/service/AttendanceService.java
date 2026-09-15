package com.posthub.hubstaff.attendance.service;

import java.util.List;
import com.posthub.hubstaff.attendance.dto.request.AttendanceClockRequest;

import com.posthub.hubstaff.attendance.dto.response.AttendanceResponseDto;

public interface AttendanceService {

    AttendanceResponseDto clockIn(Long employeeId);

    AttendanceResponseDto clockIn(Long employeeId, AttendanceClockRequest request);

    AttendanceResponseDto clockOut(Long employeeId);

    AttendanceResponseDto clockOut(Long employeeId, AttendanceClockRequest request);

    AttendanceResponseDto getToday(Long employeeId);

    AttendanceResponseDto clockInForUser(String email);

    AttendanceResponseDto clockInForUser(String email, AttendanceClockRequest request);

    AttendanceResponseDto clockOutForUser(String email);

    AttendanceResponseDto clockOutForUser(String email, AttendanceClockRequest request);

    AttendanceResponseDto getTodayForUser(String email);

    List<AttendanceResponseDto> getHistoryForUser(String email);
}
