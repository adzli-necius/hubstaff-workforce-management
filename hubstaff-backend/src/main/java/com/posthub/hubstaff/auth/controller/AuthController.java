package com.posthub.hubstaff.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.posthub.hubstaff.auth.dto.LoginRequest;
import com.posthub.hubstaff.auth.dto.LoginResponse;
import com.posthub.hubstaff.auth.service.AuthService;
import com.posthub.hubstaff.auth.dto.CreateUserRequest;
import com.posthub.hubstaff.auth.service.UserAccountService;
import com.posthub.hubstaff.common.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final UserAccountService userAccountService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "LOGIN_SUCCESS", "Login successful", authService.login(request)));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<LoginResponse.AuthenticatedUser>> createUser(
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication) {
        boolean canAssignPrivilegedRoles = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), "USER_CREATED", "User account created successfully",
            userAccountService.createUser(request, canAssignPrivilegedRoles)));
    }

}
