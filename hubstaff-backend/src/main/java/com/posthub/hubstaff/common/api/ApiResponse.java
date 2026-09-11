package com.posthub.hubstaff.common.api;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Standard response contract returned by every Hubstaff API endpoint.
 */
public record ApiResponse<T>(
        int status,
        String code,
        String message,
        String service,
        OffsetDateTime timestamp,
        T data,
        Map<String, String> errors) {

    private static final String SERVICE_NAME = "hubstaff-backend";

    public static <T> ApiResponse<T> success(int status, String code, String message, T data) {
        return new ApiResponse<>(
                status, code, message, SERVICE_NAME, OffsetDateTime.now(), data, null);
    }

    public static ApiResponse<Void> error(
            int status, String code, String message, Map<String, String> errors) {
        return new ApiResponse<>(
                status, code, message, SERVICE_NAME, OffsetDateTime.now(), null, errors);
    }
}
