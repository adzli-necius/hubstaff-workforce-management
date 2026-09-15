package com.posthub.hubstaff.attendance.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record OvertimeDecisionRequest(
        @Min(value = 0, message = "Approved minutes cannot be negative") Integer approvedMinutes,
        @Size(max = 500, message = "Manager note must not exceed 500 characters") String managerNote) {
}