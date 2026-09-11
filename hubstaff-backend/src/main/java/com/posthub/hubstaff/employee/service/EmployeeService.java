package com.posthub.hubstaff.employee.service;

import java.util.List;

import com.posthub.hubstaff.employee.dto.request.EmployeeRequestDto;
import com.posthub.hubstaff.employee.dto.response.EmployeeResponseDto;

public interface EmployeeService {

    List<EmployeeResponseDto> getAllEmployees();

    EmployeeResponseDto getEmployeeById(Long id);

    EmployeeResponseDto createEmployee(EmployeeRequestDto request);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto request);

    void deleteEmployee(Long id);


}
