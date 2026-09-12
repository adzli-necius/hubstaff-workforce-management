package com.posthub.hubstaff.attendance.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponseDto(
        Long id,
        Long employeeId,
        LocalDate attendanceDate,
        LocalDateTime clockIn,
        LocalDateTime clockOut,
        String status) {
}
