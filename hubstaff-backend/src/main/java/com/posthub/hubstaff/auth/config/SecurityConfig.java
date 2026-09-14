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
                    .requestMatchers("/api/auth/login", "/api/auth/setup-admin", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                    .requestMatchers("/api/auth/users").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}