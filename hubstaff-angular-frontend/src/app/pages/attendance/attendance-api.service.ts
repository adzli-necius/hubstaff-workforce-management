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
}
