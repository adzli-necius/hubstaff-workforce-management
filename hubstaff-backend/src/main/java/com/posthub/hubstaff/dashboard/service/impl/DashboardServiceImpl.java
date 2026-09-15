package com.posthub.hubstaff.dashboard.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.posthub.hubstaff.attendance.entity.Attendance;
import com.posthub.hubstaff.attendance.repository.AttendanceRepository;
import com.posthub.hubstaff.auth.entity.UserAccount;
import com.posthub.hubstaff.auth.repository.UserAccountRepository;
import com.posthub.hubstaff.dashboard.dto.DashboardResponse;
import com.posthub.hubstaff.dashboard.service.DashboardService;
import com.posthub.hubstaff.employee.entity.Employee;
import com.posthub.hubstaff.employee.repository.EmployeeRepository;
import com.posthub.hubstaff.leave.entity.LeaveRequest;
import com.posthub.hubstaff.leave.repository.LeaveRequestRepository;
import com.posthub.hubstaff.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String email) {
        LocalDate today = LocalDate.now();
        List<Employee> employees = employeeRepository.findAll();
        List<Attendance> attendance = attendanceRepository.findByAttendanceDateOrderByClockInAsc(today);

        List<DashboardResponse.AttendanceItem> attendanceItems = attendance.stream()
                .map(this::toAttendanceItem)
                .toList();

        int onLeaveToday = (int) employees.stream()
                .filter(employee -> leaveRequestRepository.existsByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employee.getId(), "approved", today, today))
                .count();

        List<DashboardResponse.LeaveItem> leaveItems = leaveRequestRepository.findTop5ByStatusOrderByAppliedAtDesc("pending")
                .stream()
                .map(this::toLeaveItem)
                .toList();

        UserAccount account = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("USER_ACCOUNT_NOT_FOUND", "User account not found"));

        DashboardResponse.AttendanceItem myAttendance = attendance.stream()
                .filter(item -> item.getEmployee().getId().equals(account.getEmployee().getId()))
                .map(this::toAttendanceItem)
                .findFirst()
                .orElse(null);

        return new DashboardResponse(
                today,
                (int) employees.stream().filter(employee -> "active".equalsIgnoreCase(employee.getEmploymentStatus())).count(),
                (int) attendance.stream().filter(item -> item.getClockIn() != null).count(),
                0,
                onLeaveToday,
                myAttendance,
                attendanceItems,
                leaveItems);
    }

    private DashboardResponse.AttendanceItem toAttendanceItem(Attendance attendance) {
        Employee employee = attendance.getEmployee();
        return new DashboardResponse.AttendanceItem(
                employee.getId(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getRole(),
                attendance.getClockIn(),
                attendance.getClockOut(),
                attendance.getStatus());
    }

    private DashboardResponse.LeaveItem toLeaveItem(LeaveRequest request) {
        return new DashboardResponse.LeaveItem(
                request.getId(),
                request.getEmployee().getFirstName() + " " + request.getEmployee().getLastName(),
                request.getLeaveType().getName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus());
    }
}