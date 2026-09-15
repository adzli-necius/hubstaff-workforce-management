package com.posthub.hubstaff.attendance.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public record AttendanceResponseDto(
        Long id,
        Long employeeId,
        LocalDate attendanceDate,
        LocalDateTime clockIn,
        LocalDateTime clockOut,
        String status,
        BigDecimal clockInLatitude,
        BigDecimal clockInLongitude,
        BigDecimal clockInAccuracy,
        BigDecimal clockOutLatitude,
        BigDecimal clockOutLongitude,
        BigDecimal clockOutAccuracy) {
}
