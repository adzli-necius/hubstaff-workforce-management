package com.posthub.hubstaff.leave.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.posthub.hubstaff.leave.entity.LeaveRequest;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
}