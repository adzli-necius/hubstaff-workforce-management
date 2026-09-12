package com.posthub.hubstaff.leave.service.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.posthub.hubstaff.employee.entity.Employee;
import com.posthub.hubstaff.employee.repository.EmployeeRepository;
import com.posthub.hubstaff.leave.dto.request.LeaveRequestCreateRequestDto;
import com.posthub.hubstaff.leave.dto.response.LeaveRequestResponseDto;
import com.posthub.hubstaff.leave.dto.response.LeaveRequestSummaryResponseDto;
import com.posthub.hubstaff.leave.entity.LeaveRequest;
import com.posthub.hubstaff.leave.entity.LeaveType;
import com.posthub.hubstaff.leave.repository.LeaveRequestRepository;
import com.posthub.hubstaff.leave.repository.LeaveTypeRepository;
import com.posthub.hubstaff.leave.service.LeaveRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public LeaveRequestResponseDto createLeaveRequest(
            LeaveRequestCreateRequestDto request) {

        // Validate dates
        if (request.getStartDate() == null ||
                request.getEndDate() == null) {

            throw new IllegalArgumentException(
                    "Start date and end date are required");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }

        // Find employee
        Employee employee = employeeRepository
                .findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found"));

        // Find leave type
        LeaveType leaveType = leaveTypeRepository
                .findById(request.getLeaveTypeId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Leave type not found"));

        // Check leave type is active
        if (!Boolean.TRUE.equals(leaveType.getIsActive())) {

            throw new IllegalArgumentException(
                    "Leave type is inactive");
        }

        // Calculate total leave days
        long numberOfDays = ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()
        ) + 1;

        BigDecimal totalDays =
                BigDecimal.valueOf(numberOfDays);

        // Create leave request
        LeaveRequest leaveRequest = new LeaveRequest();

        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setTotalDays(totalDays);
        leaveRequest.setReason(request.getReason());

        // New request always starts as pending
        leaveRequest.setStatus("pending");

        LeaveRequest savedRequest =
                leaveRequestRepository.save(leaveRequest);

        return mapToResponse(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getAllLeaveRequests() {

        List<LeaveRequest> leaveRequests =
                leaveRequestRepository.findAll();

        return leaveRequests.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequestResponseDto getLeaveRequestById(Long id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Leave request not found"));

        return mapToResponse(leaveRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequestSummaryResponseDto getLeaveRequestSummary() {

        LeaveRequestSummaryResponseDto response =
                new LeaveRequestSummaryResponseDto();

        response.setPending(
                leaveRequestRepository.countByStatus("pending"));

        response.setApproved(
                leaveRequestRepository.countByStatus("approved"));

        response.setRejected(
                leaveRequestRepository.countByStatus("rejected"));

        response.setTotal(
                leaveRequestRepository.count());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getLeaveRequests(
            String search,
            String status) {

        List<LeaveRequest> leaveRequests =
                leaveRequestRepository.findAll();

        return leaveRequests.stream()
                .filter(leaveRequest -> {

                    if (status == null || status.isBlank()
                            || status.equalsIgnoreCase("all")) {
                        return true;
                    }

                    return leaveRequest.getStatus()
                            .equalsIgnoreCase(status);
                })
                .filter(leaveRequest -> {

                    if (search == null || search.isBlank()) {
                        return true;
                    }

                    String searchValue =
                            search.trim().toLowerCase();

                    Employee employee =
                            leaveRequest.getEmployee();

                    String employeeName =
                            employee.getFirstName() + " "
                                    + employee.getLastName();

                    return employeeName.toLowerCase()
                            .contains(searchValue)
                            || employee.getEmployeeCode()
                            .toLowerCase()
                            .contains(searchValue);
                })
                .map(this::mapToResponse)
                .toList();
    }

    private LeaveRequestResponseDto mapToResponse(LeaveRequest leaveRequest) {

        LeaveRequestResponseDto response = new LeaveRequestResponseDto();

        response.setId(leaveRequest.getId());

        // Employee
        response.setEmployeeId(leaveRequest.getEmployee().getId());
        response.setEmployeeName(
                leaveRequest.getEmployee().getFirstName()
                + " "
                + leaveRequest.getEmployee().getLastName()
        );
        response.setEmployeeRole(leaveRequest.getEmployee().getRole());

        // Leave type
        response.setLeaveTypeId(leaveRequest.getLeaveType().getId());
        response.setLeaveTypeName(leaveRequest.getLeaveType().getName());

        // Leave request
        response.setStartDate(leaveRequest.getStartDate());
        response.setEndDate(leaveRequest.getEndDate());
        response.setTotalDays(leaveRequest.getTotalDays());
        response.setReason(leaveRequest.getReason());
        response.setStatus(leaveRequest.getStatus());
        response.setAppliedAt(leaveRequest.getAppliedAt());

        return response;
    }

}
