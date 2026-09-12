package com.posthub.hubstaff.attendance.dto.request;

import jakarta.validation.constraints.NotNull;

public record AttendanceClockRequest(
        @NotNull(message = "Employee ID is required") Long employeeId) {
}
