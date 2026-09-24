package com.posthub.hubstaff.attendance.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "attendances",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_employee_daily_attendance",
            columnNames = {"employee_id", "attendance_date"}
        )
    }
)
@Getter
@Setter
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "clock_in")
    private LocalDateTime clockIn;

    @Column(name = "clock_in_latitude", precision = 10, scale = 7)
    private java.math.BigDecimal clockInLatitude;

    @Column(name = "clock_in_longitude", precision = 10, scale = 7)
    private java.math.BigDecimal clockInLongitude;

    @Column(name = "clock_in_accuracy", precision = 10, scale = 2)
    private java.math.BigDecimal clockInAccuracy;

    @Column(name = "clock_in_location_display", length = 255)
    private String clockInLocationDisplay;

    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    @Column(name = "clock_out_latitude", precision = 10, scale = 7)
    private java.math.BigDecimal clockOutLatitude;

    @Column(name = "clock_out_longitude", precision = 10, scale = 7)
    private java.math.BigDecimal clockOutLongitude;

    @Column(name = "clock_out_accuracy", precision = 10, scale = 2)
    private java.math.BigDecimal clockOutAccuracy;

    @Column(name = "clock_out_location_display", length = 255)
    private String clockOutLocationDisplay;

    @Column(nullable = false, length = 20)
    private String status = "present";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
