package com.posthub.hubstaff.leave.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.posthub.hubstaff.leave.entity.LeaveRequest;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    long countByStatus(String status);

    boolean existsByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long employeeId, String status, LocalDate date, LocalDate sameDate);

    List<LeaveRequest> findTop5ByStatusOrderByAppliedAtDesc(String status);
}