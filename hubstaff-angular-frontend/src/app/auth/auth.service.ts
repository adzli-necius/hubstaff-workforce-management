import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';

const API_URL = 'http://localhost:8080/api/auth';
const ACCESS_TOKEN_KEY = 'hubstaff.accessToken';
const USER_KEY = 'hubstaff.user';

export interface AuthenticatedUser {
  id: number;
  employeeId: string;
  name: string;
  roles: string[];
}

interface LoginResponse {
  accessToken: string;
  expiresAt: string;
  user: AuthenticatedUser;
}

interface ApiResponse<T> {
  data: T;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  login(email: string, password: string): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${API_URL}/login`, { email, password }).pipe(
      tap(response => {
        localStorage.setItem(ACCESS_TOKEN_KEY, response.data.accessToken);
        localStorage.setItem(USER_KEY, JSON.stringify(response.data.user));
      })
    );
  }

  logout(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }

  isAuthenticated(): boolean {
    return Boolean(this.getAccessToken());
  }

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  getCurrentUser(): AuthenticatedUser | null {
    const value = localStorage.getItem(USER_KEY);
    if (!value) {
      return null;
    }

    try {
      return JSON.parse(value) as AuthenticatedUser;
    } catch {
      this.logout();
      return null;
    }
  }

  hasRole(role: string): boolean {
    return this.getCurrentUser()?.roles.includes(role.toUpperCase()) ?? false;
  }
}
