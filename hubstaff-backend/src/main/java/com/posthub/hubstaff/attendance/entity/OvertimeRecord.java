package com.posthub.hubstaff.attendance.entity;

import java.time.LocalDateTime;

import com.posthub.hubstaff.auth.entity.UserAccount;
import com.posthub.hubstaff.employee.entity.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "overtime_records")
@Getter
@Setter
public class OvertimeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    @Column(name = "overtime_start", nullable = false)
    private LocalDateTime overtimeStart;

    @Column(name = "overtime_end")
    private LocalDateTime overtimeEnd;

    @Column(name = "requested_minutes")
    private Integer requestedMinutes;

    @Column(name = "approved_minutes")
    private Integer approvedMinutes;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(length = 500)
    private String reason;

    @Column(name = "manager_note", length = 500)
    private String managerNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private UserAccount approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}