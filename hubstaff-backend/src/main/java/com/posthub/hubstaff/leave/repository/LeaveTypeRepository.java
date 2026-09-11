package com.posthub.hubstaff.leave.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.posthub.hubstaff.leave.entity.LeaveType;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
}