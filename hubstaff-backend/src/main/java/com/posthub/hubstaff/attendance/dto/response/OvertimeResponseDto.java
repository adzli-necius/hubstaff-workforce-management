package com.posthub.hubstaff.attendance.dto.response;

import java.time.LocalDateTime;

public record OvertimeResponseDto(
        Long id,
        Long employeeId,
        String employeeName,
        Long attendanceId,
        LocalDateTime overtimeStart,
        LocalDateTime overtimeEnd,
        Integer requestedMinutes,
        Integer approvedMinutes,
        String status,
        String reason,
        String managerNote,
        LocalDateTime approvedAt) {
}