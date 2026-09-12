package com.posthub.hubstaff.attendance.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.posthub.hubstaff.attendance.dto.response.AttendanceResponseDto;
import com.posthub.hubstaff.attendance.entity.Attendance;
import com.posthub.hubstaff.attendance.repository.AttendanceRepository;
import com.posthub.hubstaff.attendance.service.AttendanceService;
import com.posthub.hubstaff.common.exception.BusinessRuleException;
import com.posthub.hubstaff.common.exception.ResourceNotFoundException;
import com.posthub.hubstaff.employee.entity.Employee;
import com.posthub.hubstaff.employee.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public AttendanceResponseDto clockIn(Long employeeId) {
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
        attendance.setStatus("present");

        return toResponse(attendanceRepository.save(attendance));
    }

    @Override
    public AttendanceResponseDto clockOut(Long employeeId) {
        Attendance attendance = findTodayAttendance(employeeId);
        if (attendance.getClockOut() != null) {
            throw new BusinessRuleException("ALREADY_CLOCKED_OUT", "Employee has already clocked out today");
        }

        attendance.setClockOut(LocalDateTime.now());
        return toResponse(attendanceRepository.save(attendance));
    }

    @Override
    public AttendanceResponseDto getToday(Long employeeId) {
        return toResponse(findTodayAttendance(employeeId));
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

    private AttendanceResponseDto toResponse(Attendance attendance) {
        return new AttendanceResponseDto(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getAttendanceDate(),
                attendance.getClockIn(),
                attendance.getClockOut(),
                attendance.getStatus());
    }
}
