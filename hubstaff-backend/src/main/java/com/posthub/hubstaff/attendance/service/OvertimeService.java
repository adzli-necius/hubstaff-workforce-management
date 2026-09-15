package com.posthub.hubstaff.attendance.service;

import java.util.List;

import com.posthub.hubstaff.attendance.dto.request.OvertimeDecisionRequest;
import com.posthub.hubstaff.attendance.dto.request.OvertimeStartRequest;
import com.posthub.hubstaff.attendance.dto.response.OvertimeResponseDto;

public interface OvertimeService {

    OvertimeResponseDto startForUser(String email, OvertimeStartRequest request);

    OvertimeResponseDto endForUser(String email);

    List<OvertimeResponseDto> getHistoryForUser(String email);

    List<OvertimeResponseDto> getPending();

    OvertimeResponseDto approve(Long id, String managerEmail, OvertimeDecisionRequest request);

    OvertimeResponseDto reject(Long id, String managerEmail, OvertimeDecisionRequest request);
}