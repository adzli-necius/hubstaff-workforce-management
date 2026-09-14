package com.posthub.hubstaff.auth.dto;

import java.time.Instant;
import java.util.Set;

public record LoginResponse(
        String accessToken,
        Instant expiresAt,
        AuthenticatedUser user) {

    public record AuthenticatedUser(
            Long id,
            String employeeId,
            String name,
            Set<String> roles) {
    }
}