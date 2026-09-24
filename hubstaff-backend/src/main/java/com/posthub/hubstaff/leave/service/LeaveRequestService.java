package com.posthub.hubstaff.leave.service;

import java.util.List;

import com.posthub.hubstaff.leave.dto.request.LeaveRequestCreateRequestDto;
import com.posthub.hubstaff.leave.dto.response.LeaveRequestResponseDto;
import com.posthub.hubstaff.leave.dto.response.LeaveRequestSummaryResponseDto;

public interface LeaveRequestService {

    LeaveRequestResponseDto createLeaveRequest(
            LeaveRequestCreateRequestDto request
    );

    LeaveRequestResponseDto createLeaveRequestForUser(
            String email,
            LeaveRequestCreateRequestDto request
    );

    List<LeaveRequestResponseDto> getAllLeaveRequests();

    LeaveRequestResponseDto getLeaveRequestById(Long id);

    LeaveRequestSummaryResponseDto getLeaveRequestSummary();

    List<LeaveRequestResponseDto> getLeaveRequests(
            String search,
            String status);
}
