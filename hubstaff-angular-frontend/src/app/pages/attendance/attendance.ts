import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';

import { AuthService } from '../../auth/auth.service';
import { AttendanceApiService, AttendanceLocation, AttendanceRecord, OvertimeRecord, WorkMode } from './attendance-api.service';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './attendance.html',
  styleUrl: './attendance.css'
})
export class Attendance implements OnInit, OnDestroy {

  private readonly attendanceApi = inject(AttendanceApiService);
  private readonly authService = inject(AuthService);
  private readonly http = inject(HttpClient);

  currentTime = new Date();

  clockedIn = false;

  clockInTime: Date | null = null;

  clockOutTime: Date | null = null;
  overtimeRecord: OvertimeRecord | null = null;
  overtimeReason = '';
  isOvertimeSubmitting = false;

  showClockModal = false;

  pendingClockAction: 'in' | 'out' | null = null;
  pendingWorkMode: WorkMode = 'NORMAL';
  pendingLocation: AttendanceLocation | null = null;
  pendingLocationName = '';
  isResolvingLocation = false;
  isLocating = false;

  attendanceHistory: AttendanceRecord[] = [];
  isLoadingHistory = false;
  errorMessage = '';
  isSubmitting = false;

  private timer: ReturnType<typeof setInterval>;

  constructor() {
    this.timer = setInterval(() => {
      this.currentTime = new Date();
    }, 1000);
  }

  ngOnInit(): void {
    this.loadTodayAttendance();
    this.loadAttendanceHistory();
    this.loadOvertime();
  }

  startOvertime(): void {
    if (this.clockInTime === null || this.clockOutTime === null) {
      this.errorMessage = 'Clock out before starting overtime.';
      return;
    }

    this.isOvertimeSubmitting = true;
    this.errorMessage = '';
    this.attendanceApi.startOvertime(this.overtimeReason.trim() || undefined).subscribe({
      next: response => {
        this.overtimeRecord = response.data;
        this.overtimeReason = '';
      },
      error: error => this.errorMessage = this.getErrorMessage(error),
      complete: () => this.isOvertimeSubmitting = false
    });
  }

  endOvertime(): void {
    this.isOvertimeSubmitting = true;
    this.errorMessage = '';
    this.attendanceApi.endOvertime().subscribe({
      next: response => this.overtimeRecord = response.data,
      error: error => this.errorMessage = this.getErrorMessage(error),
      complete: () => this.isOvertimeSubmitting = false
    });
  }

  clockAction(): void {
    this.pendingClockAction = this.clockedIn ? 'out' : 'in';
    this.pendingWorkMode = 'NORMAL';
    this.pendingLocation = null;
    this.errorMessage = '';
    this.showClockModal = true;
    this.isLocating = false;
  }

  selectWorkMode(mode: WorkMode): void {
    this.pendingWorkMode = mode;
  }

  requestLocation(): void {
    if (this.pendingClockAction === 'in' && this.pendingWorkMode === 'OVERTIME'
        && (this.clockInTime === null || this.clockOutTime === null)) {
      this.errorMessage = 'Clock in and clock out normally before starting overtime.';
      return;
    }

    this.errorMessage = '';
    this.isLocating = true;

    if (!navigator.geolocation) {
      this.isLocating = false;
      this.pendingClockAction = null;
      this.errorMessage = 'This browser does not support location access.';
      return;
    }

    navigator.geolocation.getCurrentPosition(
      position => {
        this.pendingLocation = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          accuracy: position.coords.accuracy
        };
        this.isLocating = false;
        this.pendingLocationName = 'Resolving location...';
        this.showClockModal = true;
        this.resolveLocationName(this.pendingLocation);
      },
      error => {
        this.isLocating = false;
        this.pendingClockAction = null;
        this.errorMessage = error.code === error.PERMISSION_DENIED
          ? 'Location permission is required to record attendance.'
          : 'Unable to get your current location. Please try again.';
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    );
  }

  cancelClockAction(): void {
    this.showClockModal = false;

    this.pendingClockAction = null;
    this.pendingWorkMode = 'NORMAL';
    this.pendingLocation = null;
    this.pendingLocationName = '';
    this.isResolvingLocation = false;
  }

  confirmClockAction(): void {
    if (this.pendingClockAction === null || this.pendingLocation === null) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    const action = this.pendingClockAction;
    const request = (action === 'in' && this.pendingWorkMode === 'OVERTIME'
      ? this.attendanceApi.startOvertime()
      : action === 'in'
        ? this.attendanceApi.clockInForUser(this.pendingLocation, this.pendingWorkMode)
        : this.attendanceApi.clockOutForUser(this.pendingLocation)) as unknown as Observable<{ data: AttendanceRecord | OvertimeRecord }>;

    request.subscribe({
      next: response => {
        if (action === 'in' && this.pendingWorkMode === 'OVERTIME') {
          this.overtimeRecord = response.data as unknown as OvertimeRecord;
        } else {
          this.applyAttendance(response.data as AttendanceRecord);
        }
        this.loadAttendanceHistory();
        this.cancelClockAction();
      },
      error: error => this.errorMessage = this.getErrorMessage(error),
      complete: () => this.isSubmitting = false
    });
  }

  private loadTodayAttendance(): void {
    this.attendanceApi.getTodayForUser().subscribe({
      next: response => this.applyAttendance(response.data),
      error: error => {
        if (error instanceof HttpErrorResponse && error.error?.code === 'NOT_CLOCKED_IN') {
          return;
        }
        this.errorMessage = this.getErrorMessage(error);
      }
    });
  }

  private resolveLocationName(location: AttendanceLocation): void {
    this.isResolvingLocation = true;
    const url = 'https://nominatim.openstreetmap.org/reverse'
      + `?format=jsonv2&zoom=18&lat=${location.latitude}&lon=${location.longitude}`;

    this.http.get<{ display_name?: string }>(url).subscribe({
      next: response => this.pendingLocationName = response.display_name || 'Location captured',
      error: () => {
        this.pendingLocationName = 'Location captured';
        this.isResolvingLocation = false;
      },
      complete: () => this.isResolvingLocation = false
    });
  }

  private loadAttendanceHistory(): void {
    this.isLoadingHistory = true;
    this.attendanceApi.getHistoryForUser().subscribe({
      next: response => this.attendanceHistory = response.data,
      error: error => this.errorMessage = this.getErrorMessage(error),
      complete: () => this.isLoadingHistory = false
    });
  }

  private loadOvertime(): void {
    this.attendanceApi.getOvertimeHistory().subscribe({
      next: response => this.overtimeRecord = response.data.find(record => !record.overtimeEnd) ?? response.data[0] ?? null,
      error: error => this.errorMessage = this.getErrorMessage(error)
    });
  }

  get currentUserName(): string {
    return this.authService.getCurrentUser()?.name ?? 'Employee';
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
