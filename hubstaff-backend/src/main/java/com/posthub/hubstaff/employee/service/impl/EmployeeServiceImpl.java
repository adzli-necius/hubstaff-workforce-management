package com.posthub.hubstaff.employee.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.posthub.hubstaff.common.exception.ResourceNotFoundException;
import com.posthub.hubstaff.employee.dto.request.EmployeeRequestDto;
import com.posthub.hubstaff.employee.dto.response.EmployeeResponseDto;
import com.posthub.hubstaff.employee.entity.Employee;
import com.posthub.hubstaff.employee.repository.EmployeeRepository;
import com.posthub.hubstaff.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> employeeNotFound(id));

        return mapToResponse(employee);
    }

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto request) {
        Employee employee = new Employee();

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setRole(request.getRole());
        employee.setPhone(request.getPhone());

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "MANAGER_NOT_FOUND", "Manager not found with id: " + request.getManagerId()));

            employee.setManager(manager);
        }

        employee.setEmploymentStatus(request.getEmploymentStatus());
        employee.setHireDate(request.getHireDate());

        Employee savedEmployee = employeeRepository.save(employee);

        return mapToResponse(savedEmployee);
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> employeeNotFound(id));

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setRole(request.getRole());
        employee.setPhone(request.getPhone());
        
        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "MANAGER_NOT_FOUND", "Manager not found with id: " + request.getManagerId()));

            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }

        employee.setEmploymentStatus(request.getEmploymentStatus());
        employee.setHireDate(request.getHireDate());

        Employee updatedEmployee = employeeRepository.save(employee);

        return mapToResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw employeeNotFound(id);
        }

        employeeRepository.deleteById(id);
    }

    private EmployeeResponseDto mapToResponse(Employee employee) {

        EmployeeResponseDto response = new EmployeeResponseDto();

        response.setId(employee.getId());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setRole(employee.getRole());
        response.setPhone(employee.getPhone());
        response.setEmploymentStatus(employee.getEmploymentStatus());
        response.setHireDate(employee.getHireDate());

        if (employee.getManager() != null) {
            response.setManagerId(employee.getManager().getId());
        }

        return response;
    }

    private ResourceNotFoundException employeeNotFound(Long id) {
        return new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Employee not found with id: " + id);
    }
}
