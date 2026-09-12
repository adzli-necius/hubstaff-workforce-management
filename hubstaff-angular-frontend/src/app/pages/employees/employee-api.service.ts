import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/employees';

export type EmploymentStatus = 'active' | 'inactive';

export interface ApiResponse<T> {
  status: number;
  code: string;
  message: string;
  service: string;
  timestamp: string;
  data: T;
  errors: Record<string, string> | null;
}

export interface EmployeeApiModel {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  role: string;
  phone: string | null;
  managerId: number | null;
  employmentStatus: EmploymentStatus;
  hireDate: string;
}

export interface EmployeeRequest {
  employeeCode: string;
  firstName: string;
  lastName: string;
  role: string;
  phone: string;
  managerId: number | null;
  employmentStatus: EmploymentStatus;
  hireDate: string;
}

@Injectable({ providedIn: 'root' })
export class EmployeeApiService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<ApiResponse<EmployeeApiModel[]> | EmployeeApiModel[]> {
    return this.http.get<ApiResponse<EmployeeApiModel[]> | EmployeeApiModel[]>(API_URL);
  }

  create(employee: EmployeeRequest): Observable<ApiResponse<EmployeeApiModel>> {
    return this.http.post<ApiResponse<EmployeeApiModel>>(API_URL, employee);
  }

  update(id: number, employee: EmployeeRequest): Observable<ApiResponse<EmployeeApiModel>> {
    return this.http.put<ApiResponse<EmployeeApiModel>>(`${API_URL}/${id}`, employee);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${API_URL}/${id}`);
  }
}
