package com.posthub.hubstaff.leave.service;

import com.posthub.hubstaff.leave.dto.request.LeaveRequestCreateRequestDto;
import com.posthub.hubstaff.leave.dto.response.LeaveRequestResponseDto;

public interface LeaveRequestService {

    LeaveRequestResponseDto createLeaveRequest(
            LeaveRequestCreateRequestDto request
    );
}
