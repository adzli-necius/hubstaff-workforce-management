package com.posthub.hubstaff.employee.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import com.posthub.hubstaff.common.api.ApiResponse;

import com.posthub.hubstaff.employee.dto.request.EmployeeRequestDto;
import com.posthub.hubstaff.employee.dto.response.EmployeeResponseDto;
import com.posthub.hubstaff.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponseDto>>> getAllEmployees() {
        List<EmployeeResponseDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "EMPLOYEES_RETRIEVED", "Employees retrieved successfully", employees));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "EMPLOYEE_RETRIEVED", "Employee retrieved successfully", employee));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> createEmployee(
            @Valid @RequestBody EmployeeRequestDto request) {

        EmployeeResponseDto employee = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), "EMPLOYEE_CREATED", "Employee created successfully", employee));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto request) {

        EmployeeResponseDto employee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "EMPLOYEE_UPDATED", "Employee updated successfully", employee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(), "EMPLOYEE_DELETED", "Employee deleted successfully", null));
    }
}
