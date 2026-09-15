package com.posthub.hubstaff.attendance.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.posthub.hubstaff.attendance.dto.request.OvertimeDecisionRequest;
import com.posthub.hubstaff.attendance.dto.request.OvertimeStartRequest;
import com.posthub.hubstaff.attendance.dto.response.OvertimeResponseDto;
import com.posthub.hubstaff.attendance.entity.Attendance;
import com.posthub.hubstaff.attendance.entity.OvertimeRecord;
import com.posthub.hubstaff.attendance.repository.AttendanceRepository;
import com.posthub.hubstaff.attendance.repository.OvertimeRecordRepository;
import com.posthub.hubstaff.attendance.service.OvertimeService;
import com.posthub.hubstaff.auth.entity.UserAccount;
import com.posthub.hubstaff.auth.repository.UserAccountRepository;
import com.posthub.hubstaff.common.exception.BusinessRuleException;
import com.posthub.hubstaff.common.exception.ResourceNotFoundException;
import com.posthub.hubstaff.employee.entity.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OvertimeServiceImpl implements OvertimeService {

    private final OvertimeRecordRepository overtimeRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional
    public OvertimeResponseDto startForUser(String email, OvertimeStartRequest request) {
        UserAccount account = findUser(email);
        Employee employee = account.getEmployee();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(
                        employee.getId(), LocalDate.now())
                .orElseThrow(() -> new BusinessRuleException(
                        "ATTENDANCE_REQUIRED", "Clock in and clock out before starting overtime"));

        if (attendance.getClockOut() == null) {
            throw new BusinessRuleException(
                    "CLOCK_OUT_REQUIRED", "Clock out before starting overtime");
        }
        if (overtimeRepository.findFirstByEmployeeIdAndOvertimeEndIsNullOrderByOvertimeStartDesc(
                employee.getId()).isPresent()) {
            throw new BusinessRuleException(
                    "OVERTIME_ALREADY_STARTED", "An overtime session is already active");
        }

        OvertimeRecord overtime = new OvertimeRecord();
        overtime.setEmployee(employee);
        overtime.setAttendance(attendance);
        overtime.setOvertimeStart(LocalDateTime.now());
        overtime.setReason(request == null ? null : request.reason());
        return toResponse(overtimeRepository.save(overtime));
    }

    @Override
    @Transactional
    public OvertimeResponseDto endForUser(String email) {
        UserAccount account = findUser(email);
        OvertimeRecord overtime = overtimeRepository
                .findFirstByEmployeeIdAndOvertimeEndIsNullOrderByOvertimeStartDesc(account.getEmployee().getId())
                .orElseThrow(() -> new BusinessRuleException(
                        "OVERTIME_NOT_STARTED", "No active overtime session found"));

        overtime.setOvertimeEnd(LocalDateTime.now());
        overtime.setRequestedMinutes(minutesBetween(overtime.getOvertimeStart(), overtime.getOvertimeEnd()));
        return toResponse(overtimeRepository.save(overtime));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OvertimeResponseDto> getHistoryForUser(String email) {
        return overtimeRepository.findByEmployeeIdOrderByOvertimeStartDesc(findUser(email).getEmployee().getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OvertimeResponseDto> getPending() {
        return overtimeRepository.findByStatusOrderByOvertimeStartDesc("PENDING")
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public OvertimeResponseDto approve(Long id, String managerEmail, OvertimeDecisionRequest request) {
        OvertimeRecord overtime = findOvertime(id);
        validateDecision(overtime, request);
        overtime.setStatus("APPROVED");
        overtime.setApprovedMinutes(request.approvedMinutes());
        overtime.setManagerNote(request.managerNote());
        overtime.setApprovedBy(findUser(managerEmail));
        overtime.setApprovedAt(LocalDateTime.now());
        return toResponse(overtimeRepository.save(overtime));
    }

    @Override
    @Transactional
    public OvertimeResponseDto reject(Long id, String managerEmail, OvertimeDecisionRequest request) {
        OvertimeRecord overtime = findOvertime(id);
        if (!"PENDING".equals(overtime.getStatus())) {
            throw new BusinessRuleException("OVERTIME_ALREADY_DECIDED", "Overtime has already been decided");
        }
        overtime.setStatus("REJECTED");
        overtime.setApprovedMinutes(0);
        overtime.setManagerNote(request.managerNote());
        overtime.setApprovedBy(findUser(managerEmail));
        overtime.setApprovedAt(LocalDateTime.now());
        return toResponse(overtimeRepository.save(overtime));
    }

    private void validateDecision(OvertimeRecord overtime, OvertimeDecisionRequest request) {
        if (!"PENDING".equals(overtime.getStatus())) {
            throw new BusinessRuleException("OVERTIME_ALREADY_DECIDED", "Overtime has already been decided");
        }
        if (overtime.getRequestedMinutes() == null) {
            throw new BusinessRuleException("OVERTIME_NOT_ENDED", "End the overtime session before approving it");
        }
        if (request.approvedMinutes() == null || request.approvedMinutes() > overtime.getRequestedMinutes()) {
            throw new BusinessRuleException("INVALID_APPROVED_MINUTES",
                    "Approved minutes cannot exceed requested minutes");
        }
    }

    private int minutesBetween(LocalDateTime start, LocalDateTime end) {
        return (int) Math.max(1, Duration.between(start, end).toMinutes());
    }

    private UserAccount findUser(String email) {
        return userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("USER_ACCOUNT_NOT_FOUND", "User account not found"));
    }

    private OvertimeRecord findOvertime(Long id) {
        return overtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OVERTIME_NOT_FOUND", "Overtime record not found"));
    }

    private OvertimeResponseDto toResponse(OvertimeRecord overtime) {
        Employee employee = overtime.getEmployee();
        return new OvertimeResponseDto(
                overtime.getId(), employee.getId(), employee.getFirstName() + " " + employee.getLastName(),
                overtime.getAttendance().getId(), overtime.getOvertimeStart(), overtime.getOvertimeEnd(),
                overtime.getRequestedMinutes(), overtime.getApprovedMinutes(), overtime.getStatus(),
                overtime.getReason(), overtime.getManagerNote(), overtime.getApprovedAt());
    }
}