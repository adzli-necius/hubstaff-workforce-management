package com.posthub.hubstaff.employee.dto.response;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponseDto {

    private Long id;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String role;

    private String phone;

    private Long managerId;

    private String employmentStatus;

    private LocalDate hireDate;
}
