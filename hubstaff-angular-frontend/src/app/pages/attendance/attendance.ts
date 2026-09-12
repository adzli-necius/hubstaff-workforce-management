import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { EmployeeApiModel, EmployeeApiService } from '../employees/employee-api.service';
import { AttendanceApiService, AttendanceRecord } from './attendance-api.service';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './attendance.html',
  styleUrl: './attendance.css'
})
export class Attendance implements OnInit, OnDestroy {

  private readonly attendanceApi = inject(AttendanceApiService);
  private readonly employeeApi = inject(EmployeeApiService);

  currentTime = new Date();

  clockedIn = false;

  clockInTime: Date | null = null;

  clockOutTime: Date | null = null;

  showClockModal = false;

  pendingClockAction: 'in' | 'out' | null = null;

  employees: EmployeeApiModel[] = [];
  selectedEmployeeId: number | null = null;
  errorMessage = '';
  isSubmitting = false;

  private timer: ReturnType<typeof setInterval>;

  constructor() {
    this.timer = setInterval(() => {
      this.currentTime = new Date();
    }, 1000);
  }

  ngOnInit(): void {
    this.employeeApi.getAll().subscribe({
      next: response => this.employees = Array.isArray(response) ? response : response.data,
      error: () => this.errorMessage = 'Unable to load employees for attendance.'
    });
  }

  onEmployeeChange(): void {
    this.clockedIn = false;
    this.clockInTime = null;
    this.clockOutTime = null;
    this.errorMessage = '';

    if (this.selectedEmployeeId !== null) {
      this.loadTodayAttendance(this.selectedEmployeeId);
    }
  }

  clockAction(): void {
    if (this.selectedEmployeeId === null) {
      this.errorMessage = 'Select an employee before clocking in or out.';
      return;
    }

    this.pendingClockAction = this.clockedIn ? 'out' : 'in';

    this.showClockModal = true;
  }

  cancelClockAction(): void {
    this.showClockModal = false;

    this.pendingClockAction = null;
  }

  confirmClockAction(): void {
    if (this.selectedEmployeeId === null || this.pendingClockAction === null) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    const action = this.pendingClockAction;
    const request = action === 'in'
      ? this.attendanceApi.clockIn(this.selectedEmployeeId)
      : this.attendanceApi.clockOut(this.selectedEmployeeId);

    request.subscribe({
      next: response => {
        this.applyAttendance(response.data);
        this.cancelClockAction();
      },
      error: error => this.errorMessage = this.getErrorMessage(error),
      complete: () => this.isSubmitting = false
    });
  }

  private loadTodayAttendance(employeeId: number): void {
    this.attendanceApi.getToday(employeeId).subscribe({
      next: response => this.applyAttendance(response.data),
      error: error => {
        if (error instanceof HttpErrorResponse && error.error?.code === 'NOT_CLOCKED_IN') {
          return;
        }
        this.errorMessage = this.getErrorMessage(error);
      }
    });
  }

  private applyAttendance(attendance: AttendanceRecord): void {
    this.clockInTime = attendance.clockIn ? new Date(attendance.clockIn) : null;
    this.clockOutTime = attendance.clockOut ? new Date(attendance.clockOut) : null;
    this.clockedIn = this.clockInTime !== null && this.clockOutTime === null;
  }

  private getErrorMessage(error: unknown): string {
    const apiError = error instanceof HttpErrorResponse
      ? error.error as { message?: string; errors?: Record<string, string> } | null
      : null;
    const fieldErrors = apiError?.errors ? Object.values(apiError.errors).join(' ') : '';
    return fieldErrors || apiError?.message || 'Unable to update attendance.';
  }

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }
}
