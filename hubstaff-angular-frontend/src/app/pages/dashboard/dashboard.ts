import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AuthService } from '../../auth/auth.service';
import { DashboardApiService, DashboardAttendance, DashboardData, DashboardLeaveRequest } from './dashboard-api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit, OnDestroy {
  private readonly dashboardApi = inject(DashboardApiService);
  private readonly authService = inject(AuthService);

  currentTime = new Date();
  search = '';
  isLoading = true;
  errorMessage = '';
  dashboard: DashboardData | null = null;

  private timer: ReturnType<typeof setInterval>;

  constructor() {
    this.timer = setInterval(() => this.currentTime = new Date(), 1000);
  }

  ngOnInit(): void {
    this.dashboardApi.getDashboard().subscribe({
      next: response => {
        this.dashboard = response.data;
        this.isLoading = false;
      },
      error: error => {
        this.errorMessage = this.getErrorMessage(error);
        this.isLoading = false;
      }
    });
  }

  get userName(): string {
    return this.authService.getCurrentUser()?.name ?? 'Employee';
  }

  get employees(): DashboardAttendance[] {
    return this.dashboard?.attendance ?? [];
  }

  get leaveRequests(): DashboardLeaveRequest[] {
    return this.dashboard?.leaveRequests ?? [];
  }

  get filteredEmployees(): DashboardAttendance[] {
    const query = this.search.toLowerCase().trim();
    if (!query) return this.employees;
    return this.employees.filter(employee =>
      employee.employeeName.toLowerCase().includes(query) ||
      (employee.role ?? '').toLowerCase().includes(query) ||
      this.displayStatus(employee.status).toLowerCase().includes(query));
  }

  get presentCount(): number {
    return this.dashboard?.presentToday ?? 0;
  }

  get leaveCount(): number {
    return this.dashboard?.onLeaveToday ?? 0;
  }

  get lateCount(): number {
    return this.dashboard?.lateToday ?? 0;
  }

  get workingHours(): string {
    const attendance = this.dashboard?.myAttendance;
    if (!attendance?.clockIn) return '--';
    const end = attendance.clockOut ? new Date(attendance.clockOut) : this.currentTime;
    const minutes = Math.max(0, Math.floor((end.getTime() - new Date(attendance.clockIn).getTime()) / 60000));
    return `${Math.floor(minutes / 60)}h ${minutes % 60}m`;
  }

  displayStatus(status: string): 'Present' | 'On Leave' {
    return status.toLowerCase() === 'on_leave' ? 'On Leave' : 'Present';
  }

  getInitials(name: string): string {
    return name.split(' ').map(word => word.charAt(0)).join('').slice(0, 2).toUpperCase();
  }

  formatLeaveDates(leave: DashboardLeaveRequest): string {
    return `${leave.startDate} - ${leave.endDate}`;
  }

  private getErrorMessage(error: unknown): string {
    const apiError = error instanceof HttpErrorResponse
      ? error.error as { message?: string } | null
      : null;
    return apiError?.message || 'Unable to load dashboard data.';
  }

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }
}
