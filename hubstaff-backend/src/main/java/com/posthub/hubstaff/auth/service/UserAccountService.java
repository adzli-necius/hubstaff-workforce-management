package com.posthub.hubstaff.auth.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.posthub.hubstaff.auth.dto.CreateUserRequest;
import com.posthub.hubstaff.auth.dto.LoginResponse;
import com.posthub.hubstaff.auth.entity.Role;
import com.posthub.hubstaff.auth.entity.UserAccount;
import com.posthub.hubstaff.auth.entity.InitialAdminBootstrapState;
import com.posthub.hubstaff.auth.repository.InitialAdminBootstrapRepository;
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
    private final InitialAdminBootstrapRepository initialAdminBootstrapRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse.AuthenticatedUser createInitialAdmin(
            String employeeCode,
            String firstName,
            String lastName,
            String phone,
            java.time.LocalDate hireDate,
            String emailAddress,
            String password) {
        if (userAccountRepository.existsAdminAccount()) {
            throw new BusinessRuleException(
                    "INITIAL_ADMIN_EXISTS", "An administrator account already exists");
        }

        Role adminRole = roleRepository.findByNameIgnoreCase("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });

        String email = emailAddress.trim().toLowerCase(Locale.ROOT);
        if (userAccountRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new BusinessRuleException(
                    "EMAIL_ALREADY_EXISTS", "A user with this email already exists");
        }

        Employee employee = new Employee();
        employee.setEmployeeCode(employeeCode.trim());
        employee.setFirstName(firstName.trim());
        employee.setLastName(lastName.trim());
        employee.setRole("ADMIN");
        employee.setPhone(phone == null ? null : phone.trim());
        employee.setEmploymentStatus("active");
        employee.setHireDate(hireDate);

        employee = employeeRepository.save(employee);
        UserAccount account = new UserAccount();
        account.setEmployee(employee);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(password));
        account.getRoles().add(adminRole);

        UserAccount savedAccount = userAccountRepository.save(account);
        markInitialAdminBootstrapComplete();
        return toResponse(savedAccount);
    }

    @Transactional
    public void markInitialAdminBootstrapComplete() {
        if (initialAdminBootstrapRepository.existsById(InitialAdminBootstrapState.INITIAL_ADMIN_ID)) {
            return;
        }
        InitialAdminBootstrapState bootstrap = new InitialAdminBootstrapState();
        bootstrap.setId(InitialAdminBootstrapState.INITIAL_ADMIN_ID);
        bootstrap.setCompletedAt(java.time.LocalDateTime.now());
        initialAdminBootstrapRepository.save(bootstrap);
    }

    @Transactional
    public LoginResponse.AuthenticatedUser createUser(CreateUserRequest request, boolean canAssignPrivilegedRoles) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        Set<String> requestedRoles = request.getRoles().stream()
                .map(String::trim)
                .map(name -> name.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());

        Set<String> allowedRoles = Set.of("ADMIN", "MANAGER", "EMPLOYEE");
        if (!allowedRoles.containsAll(requestedRoles)) {
            throw new BusinessRuleException(
                    "INVALID_ROLE", "Only ADMIN, MANAGER, and EMPLOYEE roles can be assigned");
        }
        if (requestedRoles.size() != 1) {
            throw new BusinessRuleException(
                    "INVALID_ROLE_ASSIGNMENT", "Each user account must have exactly one role");
        }
        if (!canAssignPrivilegedRoles && !requestedRoles.equals(Set.of("EMPLOYEE"))) {
            throw new BusinessRuleException(
                    "ROLE_NOT_ALLOWED", "Managers can create employee accounts only");
        }

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

        Set<Role> roles = requestedRoles.stream()
                .map(this::findOrCreateRole)
                .collect(Collectors.toCollection(HashSet::new));

        UserAccount account = new UserAccount();
        account.setEmployee(employee);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRoles(roles);

        UserAccount saved = userAccountRepository.save(account);
        return toResponse(saved);
    }

    private Role findOrCreateRole(String name) {
        return roleRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return roleRepository.save(role);
                });
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
