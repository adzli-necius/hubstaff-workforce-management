package com.posthub.hubstaff.auth.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.posthub.hubstaff.auth.dto.CreateUserRequest;
import com.posthub.hubstaff.auth.dto.LoginResponse;
import com.posthub.hubstaff.auth.dto.SetupAdminRequest;
import com.posthub.hubstaff.auth.entity.Role;
import com.posthub.hubstaff.auth.entity.UserAccount;
import com.posthub.hubstaff.auth.repository.RoleRepository;
import com.posthub.hubstaff.auth.repository.UserAccountRepository;
import com.posthub.hubstaff.common.exception.BusinessRuleException;
import com.posthub.hubstaff.common.exception.ResourceNotFoundException;
import com.posthub.hubstaff.employee.entity.Employee;
import com.posthub.hubstaff.employee.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.bootstrap-key}")
    private String bootstrapKey;

    @Transactional
    public LoginResponse.AuthenticatedUser setupAdmin(SetupAdminRequest request) {
        if (!bootstrapKey.equals(request.getSetupKey())) {
            throw new BusinessRuleException("INVALID_SETUP_KEY", "The setup key is invalid");
        }
        if (userAccountRepository.existsAdminAccount()) {
            throw new BusinessRuleException(
                    "ADMIN_SETUP_CLOSED", "Initial admin setup is already completed");
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EMPLOYEE_NOT_FOUND", "Employee not found: " + request.getEmployeeId()));

        Role adminRole = roleRepository.findByNameIgnoreCase("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(email).orElseGet(UserAccount::new);
        if (account.getId() != null && !account.getEmployee().getId().equals(employee.getId())) {
            throw new BusinessRuleException(
                    "EMAIL_EMPLOYEE_MISMATCH", "The email belongs to a different employee");
        }
        if (account.getId() == null && userAccountRepository.existsByEmployeeId(employee.getId())) {
            throw new BusinessRuleException(
                "EMPLOYEE_ACCOUNT_EXISTS",
                "This employee already has a user account; use that account email");
        }

        account.setEmployee(employee);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.getRoles().add(adminRole);

        return toResponse(userAccountRepository.save(account));
        }

    @Transactional
    public LoginResponse.AuthenticatedUser createUser(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userAccountRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new BusinessRuleException("EMAIL_ALREADY_EXISTS", "A user with this email already exists");
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EMPLOYEE_NOT_FOUND", "Employee not found: " + request.getEmployeeId()));

        if (userAccountRepository.existsByEmployeeId(employee.getId())) {
            throw new BusinessRuleException(
                    "EMPLOYEE_ACCOUNT_EXISTS", "This employee already has a user account");
        }

        Set<Role> roles = request.getRoles().stream()
                .map(String::trim)
                .map(name -> name.toUpperCase(Locale.ROOT))
                .map(name -> roleRepository.findByNameIgnoreCase(name)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "ROLE_NOT_FOUND", "Role not found: " + name)))
                .collect(Collectors.toCollection(HashSet::new));

        UserAccount account = new UserAccount();
        account.setEmployee(employee);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRoles(roles);

        UserAccount saved = userAccountRepository.save(account);
        return toResponse(saved);
    }

    private LoginResponse.AuthenticatedUser toResponse(UserAccount account) {
        Employee employee = account.getEmployee();
        return new LoginResponse.AuthenticatedUser(
                account.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName() + " " + employee.getLastName(),
                account.getRoles().stream()
                        .map(Role::getName)
                        .map(name -> name.toUpperCase(Locale.ROOT))
                        .collect(Collectors.toCollection(java.util.LinkedHashSet::new)));
    }
}