import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LeaveRequestResponseDto {
  id: number;

  employeeId: number;
  employeeName: string;
  employeeRole: string;

  leaveTypeId: number;
  leaveTypeName: string;

  startDate: string;
  endDate: string;
  totalDays: number;
  reason: string;
  status: string;
  appliedAt: string;
}

export interface LeaveRequestSummaryResponseDto {
  pending: number;
  approved: number;
  rejected: number;
  total: number;
}

export interface ApiResponse<T> {
  status: number;
  code: string;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class LeaveService {

  private apiUrl = 'http://localhost:8080/api/leave-requests';

  constructor(private http: HttpClient) {}

  getAllLeaveRequests(): Observable<ApiResponse<LeaveRequestResponseDto[]>> {
    return this.http.get<ApiResponse<LeaveRequestResponseDto[]>>(
      this.apiUrl
    );
  }

  getLeaveRequestById(
    id: number
  ): Observable<ApiResponse<LeaveRequestResponseDto>> {
    return this.http.get<ApiResponse<LeaveRequestResponseDto>>(
      `${this.apiUrl}/${id}`
    );
  }

  getSummary(): Observable<ApiResponse<LeaveRequestSummaryResponseDto>> {
    return this.http.get<ApiResponse<LeaveRequestSummaryResponseDto>>(
      `${this.apiUrl}/summary`
    );
  }

  searchLeaveRequests(
    search?: string,
    status?: string
  ): Observable<ApiResponse<LeaveRequestResponseDto[]>> {

    let params = new HttpParams();

    if (search) {
      params = params.set('search', search);
    }

    if (status && status !== 'All') {
      params = params.set('status', status.toLowerCase());
    }

    return this.http.get<ApiResponse<LeaveRequestResponseDto[]>>(
      `${this.apiUrl}/search`,
      { params }
    );
  }
}