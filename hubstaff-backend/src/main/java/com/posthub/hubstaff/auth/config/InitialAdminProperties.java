package com.posthub.hubstaff.auth.config;

import java.time.LocalDate;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "bootstrap.admin")
@Getter
@Setter
public class InitialAdminProperties {

    private boolean enabled;
    private String email;
    private String password;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate hireDate;
}
