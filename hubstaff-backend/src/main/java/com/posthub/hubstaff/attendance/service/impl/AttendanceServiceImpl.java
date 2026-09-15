package com.posthub.hubstaff.attendance.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.posthub.hubstaff.attendance.dto.response.AttendanceResponseDto;
import com.posthub.hubstaff.attendance.dto.request.AttendanceClockRequest;
import com.posthub.hubstaff.attendance.entity.Attendance;
import com.posthub.hubstaff.attendance.repository.AttendanceRepository;
import com.posthub.hubstaff.attendance.service.AttendanceService;
import com.posthub.hubstaff.common.exception.BusinessRuleException;
import com.posthub.hubstaff.common.exception.ResourceNotFoundException;
import com.posthub.hubstaff.employee.entity.Employee;
import com.posthub.hubstaff.employee.repository.EmployeeRepository;
import com.posthub.hubstaff.auth.entity.UserAccount;
import com.posthub.hubstaff.auth.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public AttendanceResponseDto clockIn(Long employeeId) {
        return clockIn(employeeId, null);
    }

    @Override
    public AttendanceResponseDto clockIn(Long employeeId, AttendanceClockRequest request) {
        LocalDate today = LocalDate.now();
        if (attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, today).isPresent()) {
            throw new BusinessRuleException("ALREADY_CLOCKED_IN", "Employee has already clocked in today");
        }

        Employee employee = findEmployee(employeeId);
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(today);
        attendance.setClockIn(now);
        if (request != null) {
            attendance.setClockInLatitude(request.latitude());
            attendance.setClockInLongitude(request.longitude());
            attendance.setClockInAccuracy(request.accuracy());
        }
        attendance.setStatus(toAttendanceStatus(request));

        return toResponse(attendanceRepository.save(attendance));
    }

    @Override
    public AttendanceResponseDto clockOut(Long employeeId) {
        return clockOut(employeeId, null);
    }

    @Override
    public AttendanceResponseDto clockOut(Long employeeId, AttendanceClockRequest request) {
        Attendance attendance = findTodayAttendance(employeeId);
        if (attendance.getClockOut() != null) {
            throw new BusinessRuleException("ALREADY_CLOCKED_OUT", "Employee has already clocked out today");
        }

        attendance.setClockOut(LocalDateTime.now());
        if (request != null) {
            attendance.setClockOutLatitude(request.latitude());
            attendance.setClockOutLongitude(request.longitude());
            attendance.setClockOutAccuracy(request.accuracy());
        }
        return toResponse(attendanceRepository.save(attendance));
    }

    @Override
    public AttendanceResponseDto getToday(Long employeeId) {
        return toResponse(findTodayAttendance(employeeId));
    }

    @Override
    public AttendanceResponseDto clockInForUser(String email) {
        return clockIn(findUser(email).getEmployee().getId());
    }

    @Override
    public AttendanceResponseDto clockInForUser(String email, AttendanceClockRequest request) {
        return clockIn(findUser(email).getEmployee().getId(), request);
    }

    @Override
    public AttendanceResponseDto clockOutForUser(String email) {
        return clockOut(findUser(email).getEmployee().getId());
    }

    @Override
    public AttendanceResponseDto clockOutForUser(String email, AttendanceClockRequest request) {
        return clockOut(findUser(email).getEmployee().getId(), request);
    }

    @Override
    public AttendanceResponseDto getTodayForUser(String email) {
        return getToday(findUser(email).getEmployee().getId());
    }

    @Override
    public List<AttendanceResponseDto> getHistoryForUser(String email) {
        Long employeeId = findUser(email).getEmployee().getId();
        return attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Attendance findTodayAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now())
                .orElseThrow(() -> new BusinessRuleException(
                        "NOT_CLOCKED_IN", "Employee has not clocked in today"));
    }

    private Employee findEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EMPLOYEE_NOT_FOUND", "Employee not found with id: " + employeeId));
    }

    private UserAccount findUser(String email) {
        return userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USER_ACCOUNT_NOT_FOUND", "User account not found"));
    }

    private String toAttendanceStatus(AttendanceClockRequest request) {
        if (request != null && "WORKING_ON_LEAVE".equalsIgnoreCase(request.workMode())) {
            return "on_leave";
        }
        return "present";
    }

    private AttendanceResponseDto toResponse(Attendance attendance) {
        return new AttendanceResponseDto(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getAttendanceDate(),
                attendance.getClockIn(),
                attendance.getClockOut(),
                attendance.getStatus(),
                attendance.getClockInLatitude(),
                attendance.getClockInLongitude(),
                attendance.getClockInAccuracy(),
                attendance.getClockOutLatitude(),
                attendance.getClockOutLongitude(),
                attendance.getClockOutAccuracy());
    }
}
