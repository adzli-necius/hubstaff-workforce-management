package com.posthub.hubstaff.attendance.service;

import com.posthub.hubstaff.attendance.dto.response.AttendanceResponseDto;

public interface AttendanceService {

    AttendanceResponseDto clockIn(Long employeeId);

    AttendanceResponseDto clockOut(Long employeeId);

    AttendanceResponseDto getToday(Long employeeId);
}
