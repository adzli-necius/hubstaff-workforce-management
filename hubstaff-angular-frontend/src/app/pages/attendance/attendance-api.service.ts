import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApiResponse } from '../employees/employee-api.service';

const API_URL = 'http://localhost:8080/api/attendance';

export interface AttendanceRecord {
  id: number;
  employeeId: number;
  attendanceDate: string;
  clockIn: string | null;
  clockOut: string | null;
  status: string;
  clockInLatitude: number | null;
  clockInLongitude: number | null;
  clockInAccuracy: number | null;
  clockInLocationDisplay: string | null;
  clockOutLatitude: number | null;
  clockOutLongitude: number | null;
  clockOutAccuracy: number | null;
  clockOutLocationDisplay: string | null;
}

export interface AttendanceLocation {
  latitude: number;
  longitude: number;
  accuracy: number;
}

export type WorkMode = 'NORMAL' | 'OVERTIME' | 'WORKING_ON_LEAVE';

export interface OvertimeRecord {
  id: number;
  employeeId: number;
  employeeName: string;
  attendanceId: number;
  overtimeStart: string;
  overtimeEnd: string | null;
  requestedMinutes: number | null;
  approvedMinutes: number | null;
  status: string;
  reason: string | null;
  managerNote: string | null;
  approvedAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class AttendanceApiService {
  private readonly http = inject(HttpClient);

  clockIn(employeeId: number): Observable<ApiResponse<AttendanceRecord>> {
    return this.http.post<ApiResponse<AttendanceRecord>>(`${API_URL}/clock-in`, { employeeId });
  }

  clockOut(employeeId: number): Observable<ApiResponse<AttendanceRecord>> {
    return this.http.post<ApiResponse<AttendanceRecord>>(`${API_URL}/clock-out`, { employeeId });
  }

  getToday(employeeId: number): Observable<ApiResponse<AttendanceRecord>> {
    return this.http.get<ApiResponse<AttendanceRecord>>(`${API_URL}/today`, { params: { employeeId } });
  }

  clockInForUser(location: AttendanceLocation, workMode: WorkMode): Observable<ApiResponse<AttendanceRecord>> {
    return this.http.post<ApiResponse<AttendanceRecord>>(`${API_URL}/me/clock-in`, { ...location, workMode });
  }

  clockOutForUser(location: AttendanceLocation): Observable<ApiResponse<AttendanceRecord>> {
    return this.http.post<ApiResponse<AttendanceRecord>>(`${API_URL}/me/clock-out`, location);
  }

  getTodayForUser(): Observable<ApiResponse<AttendanceRecord>> {
    return this.http.get<ApiResponse<AttendanceRecord>>(`${API_URL}/me/today`);
  }

  getHistoryForUser(): Observable<ApiResponse<AttendanceRecord[]>> {
    return this.http.get<ApiResponse<AttendanceRecord[]>>(`${API_URL}/me/history`);
  }

  startOvertime(reason?: string): Observable<ApiResponse<OvertimeRecord>> {
    return this.http.post<ApiResponse<OvertimeRecord>>(`${API_URL}/me/overtime/start`, { reason });
  }

  endOvertime(): Observable<ApiResponse<OvertimeRecord>> {
    return this.http.post<ApiResponse<OvertimeRecord>>(`${API_URL}/me/overtime/end`, {});
  }

  getOvertimeHistory(): Observable<ApiResponse<OvertimeRecord[]>> {
    return this.http.get<ApiResponse<OvertimeRecord[]>>(`${API_URL}/me/overtime`);
  }

  getPendingOvertime(): Observable<ApiResponse<OvertimeRecord[]>> {
    return this.http.get<ApiResponse<OvertimeRecord[]>>('http://localhost:8080/api/overtime/pending');
  }

  approveOvertime(id: number, approvedMinutes: number, managerNote: string): Observable<ApiResponse<OvertimeRecord>> {
    return this.http.patch<ApiResponse<OvertimeRecord>>(
      `http://localhost:8080/api/overtime/${id}/approve`, { approvedMinutes, managerNote });
  }

  rejectOvertime(id: number, managerNote: string): Observable<ApiResponse<OvertimeRecord>> {
    return this.http.patch<ApiResponse<OvertimeRecord>>(
      `http://localhost:8080/api/overtime/${id}/reject`, { approvedMinutes: 0, managerNote });
  }
}
