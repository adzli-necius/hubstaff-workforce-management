package com.posthub.hubstaff.leave.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveRequestSummaryResponseDto {

    private long pending;
    private long approved;
    private long rejected;
    private long total;
}
