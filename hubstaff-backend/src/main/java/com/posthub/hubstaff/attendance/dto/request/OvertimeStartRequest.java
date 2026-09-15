package com.posthub.hubstaff.attendance.dto.request;

import jakarta.validation.constraints.Size;

public record OvertimeStartRequest(
        @Size(max = 500, message = "Reason must not exceed 500 characters") String reason) {
}