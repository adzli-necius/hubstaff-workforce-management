package com.posthub.hubstaff.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.posthub.hubstaff.attendance.entity.OvertimeRecord;

public interface OvertimeRecordRepository extends JpaRepository<OvertimeRecord, Long> {

    Optional<OvertimeRecord> findFirstByEmployeeIdAndOvertimeEndIsNullOrderByOvertimeStartDesc(Long employeeId);

    List<OvertimeRecord> findByEmployeeIdOrderByOvertimeStartDesc(Long employeeId);

    List<OvertimeRecord> findByStatusOrderByOvertimeStartDesc(String status);
}