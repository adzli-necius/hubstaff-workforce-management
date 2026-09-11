package com.posthub.hubstaff.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.posthub.hubstaff.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}