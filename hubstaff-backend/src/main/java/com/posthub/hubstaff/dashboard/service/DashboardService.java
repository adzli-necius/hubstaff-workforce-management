package com.posthub.hubstaff.dashboard.service;

import com.posthub.hubstaff.dashboard.dto.DashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboard(String email);
}