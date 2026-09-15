import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/dashboard';

export interface DashboardAttendance {
  employeeId: number;
  employeeName: string;
  role: string | null;
  clockIn: string | null;
  clockOut: string | null;
  status: string;
}

export interface DashboardLeaveRequest {
  id: number;
  employeeName: string;
  leaveType: string;
  startDate: string;
  endDate: string;
  status: string;
}

export interface DashboardData {
  date: string;
  totalEmployees: number;
  presentToday: number;
  lateToday: number;
  onLeaveToday: number;
  myAttendance: DashboardAttendance | null;
  attendance: DashboardAttendance[];
  leaveRequests: DashboardLeaveRequest[];
}

interface ApiResponse<T> {
  data: T;
}

@Injectable({ providedIn: 'root' })
export class DashboardApiService {
  private readonly http = inject(HttpClient);

  getDashboard(): Observable<ApiResponse<DashboardData>> {
    return this.http.get<ApiResponse<DashboardData>>(API_URL);
  }
}