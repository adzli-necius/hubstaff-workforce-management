package com.posthub.hubstaff.leave.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveRequestResponseDto {

    private Long id;

    private Long employeeId;
    private String employeeName;
    private String employeeRole;

    private Long leaveTypeId;
    private String leaveTypeName;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDays;
    private String reason;
    private String status;
    private LocalDateTime appliedAt;
}
