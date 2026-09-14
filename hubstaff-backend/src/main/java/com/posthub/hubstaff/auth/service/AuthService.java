package com.posthub.hubstaff.auth.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.posthub.hubstaff.auth.dto.LoginRequest;
import com.posthub.hubstaff.auth.dto.LoginResponse;
import com.posthub.hubstaff.auth.entity.Role;
import com.posthub.hubstaff.auth.entity.UserAccount;
import com.posthub.hubstaff.auth.repository.UserAccountRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-ms:900000}")
    private long expirationMs;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!account.isEnabled() || account.isLocked()) {
            throw new BadCredentialsException("This account is unavailable");
        }

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        Instant expiresAt = Instant.now().plusMillis(expirationMs);
        String token = Jwts.builder()
                .subject(account.getId().toString())
                .claim("email", account.getEmail())
                .claim("roles", roleNames(account))
                .issuedAt(java.util.Date.from(Instant.now()))
                .expiration(java.util.Date.from(expiresAt))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        var employee = account.getEmployee();
        return new LoginResponse(token, expiresAt, new LoginResponse.AuthenticatedUser(
                account.getId(), employee.getEmployeeCode(),
                employee.getFirstName() + " " + employee.getLastName(), roleNames(account)));
    }

    private Set<String> roleNames(UserAccount account) {
        return account.getRoles().stream()
                .map(Role::getName)
                .filter(name -> name != null && !name.isBlank())
                .map(name -> name.toUpperCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}