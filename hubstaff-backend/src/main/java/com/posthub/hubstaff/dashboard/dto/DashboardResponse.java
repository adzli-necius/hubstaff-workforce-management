package com.posthub.hubstaff.dashboard.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DashboardResponse(
        LocalDate date,
        int totalEmployees,
        int presentToday,
        int lateToday,
        int onLeaveToday,
        AttendanceItem myAttendance,
        List<AttendanceItem> attendance,
        List<LeaveItem> leaveRequests) {

    public record AttendanceItem(
            Long employeeId,
            String employeeName,
            String role,
            LocalDateTime clockIn,
            LocalDateTime clockOut,
            String status) {
    }

    public record LeaveItem(
            Long id,
            String employeeName,
            String leaveType,
            LocalDate startDate,
            LocalDate endDate,
            String status) {
    }
}