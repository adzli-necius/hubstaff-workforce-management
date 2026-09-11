package com.posthub.hubstaff.employee.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequestDto {

    @NotBlank(message = "Employee code is required")
    @Size(max = 20, message = "Employee code must not exceed 20 characters")
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Role is required")
    @Size(max = 50, message = "Role must not exceed 50 characters")
    private String role;

    @Size(max = 30, message = "Phone must not exceed 30 characters")
    private String phone;

    private Long managerId;

    @NotBlank(message = "Employment status is required")
    @Size(max = 20, message = "Employment status must not exceed 20 characters")
    private String employmentStatus;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;
}
