package com.posthub.hubstaff.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.posthub.hubstaff.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Custom query methods can be defined here if needed
    
}
