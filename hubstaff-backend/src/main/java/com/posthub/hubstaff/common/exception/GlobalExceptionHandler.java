package com.posthub.hubstaff.common.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.posthub.hubstaff.common.api.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(BusinessRuleException exception) {
        log.warn("Business rule prevented request: code={}, message={}", exception.getCode(), exception.getMessage());
        return error(HttpStatus.CONFLICT, exception.getCode(), exception.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException exception) {
        return error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password", null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException exception) {
        log.warn("Resource not found: code={}, message={}", exception.getCode(), exception.getMessage());
        return error(HttpStatus.NOT_FOUND, exception.getCode(), exception.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Request validation failed: {}", fieldErrors);
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedRequest(HttpMessageNotReadableException exception) {
        log.warn("Malformed request body", exception);
        return error(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request body is invalid", null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(DataIntegrityViolationException exception) {
        log.error("Database integrity constraint was violated", exception);
        return error(HttpStatus.CONFLICT, "DATA_CONFLICT", "The request conflicts with existing data", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedError(Exception exception) {
        log.error("Unhandled server error", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred", null);
    }

    private ResponseEntity<ApiResponse<Void>> error(
            HttpStatus status, String code, String message, Map<String, String> errors) {
        return ResponseEntity.status(status).body(ApiResponse.error(status.value(), code, message, errors));
    }
}
