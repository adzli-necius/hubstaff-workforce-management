package com.posthub.hubstaff.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.posthub.hubstaff.auth.security.JwtAuthenticationFilter;
import com.posthub.hubstaff.auth.repository.UserAccountRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserAccountRepository userAccountRepository;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserAccountRepository userAccountRepository) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userAccountRepository = userAccountRepository;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

        @Bean
        UserDetailsService userDetailsService() {
        return username -> userAccountRepository.findByEmailIgnoreCase(username)
            .map(account -> User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .disabled(!account.isEnabled())
                .accountLocked(account.isLocked())
                .roles(account.getRoles().stream()
                    .map(role -> role.getName().toUpperCase())
                    .toArray(String[]::new))
                .build())
            .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                "User account not found"));
        }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                    .requestMatchers("/api/auth/users").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
                    .requestMatchers("/api/employees/**").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/leave-requests").authenticated()
                    .requestMatchers("/api/leave-requests/**").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers("/api/overtime/**").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers("/api/attendance/me/**").authenticated()
                    .requestMatchers("/api/attendance/clock-in", "/api/attendance/clock-out", "/api/attendance/today").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
