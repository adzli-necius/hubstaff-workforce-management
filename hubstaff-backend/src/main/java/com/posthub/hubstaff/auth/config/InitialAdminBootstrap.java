package com.posthub.hubstaff.auth.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.posthub.hubstaff.auth.entity.InitialAdminBootstrapState;
import com.posthub.hubstaff.auth.repository.InitialAdminBootstrapRepository;
import com.posthub.hubstaff.auth.repository.UserAccountRepository;
import com.posthub.hubstaff.auth.service.UserAccountService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties(InitialAdminProperties.class)
@Slf4j
public class InitialAdminBootstrap {

    @Bean
    ApplicationRunner createInitialAdmin(
            InitialAdminProperties properties,
            InitialAdminBootstrapRepository initialAdminBootstrapRepository,
            UserAccountRepository userAccountRepository,
            UserAccountService userAccountService) {
        return arguments -> {
            if (!properties.isEnabled()) {
                return;
            }
            if (initialAdminBootstrapRepository.existsById(InitialAdminBootstrapState.INITIAL_ADMIN_ID)) {
                log.info("Initial-admin bootstrap was already completed; no account was created.");
                return;
            }
            if (userAccountRepository.existsAdminAccount()) {
                userAccountService.markInitialAdminBootstrapComplete();
                log.info("An administrator already exists; initial-admin bootstrap was marked complete.");
                return;
            }

            validate(properties);
            userAccountService.createInitialAdmin(
                    properties.getEmployeeCode(),
                    properties.getFirstName(),
                    properties.getLastName(),
                    properties.getPhone(),
                    properties.getHireDate(),
                    properties.getEmail(),
                    properties.getPassword());
            log.info("Initial administrator account created successfully.");
        };
    }

    private void validate(InitialAdminProperties properties) {
        if (!StringUtils.hasText(properties.getEmployeeCode())
                || !StringUtils.hasText(properties.getFirstName())
                || !StringUtils.hasText(properties.getLastName())
                || !StringUtils.hasText(properties.getEmail())
                || !StringUtils.hasText(properties.getPassword())
                || properties.getHireDate() == null) {
            throw new IllegalStateException(
                    "BOOTSTRAP_ADMIN_ENABLED requires INITIAL_ADMIN_EMAIL, INITIAL_ADMIN_PASSWORD, "
                    + "INITIAL_ADMIN_EMPLOYEE_CODE, INITIAL_ADMIN_FIRST_NAME, INITIAL_ADMIN_LAST_NAME, "
                    + "and INITIAL_ADMIN_HIRE_DATE");
        }
    }
}
