import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { EmployeeApiModel, EmployeeApiService } from '../employees/employee-api.service';
import { UserAccountService } from '../../auth/user-account.service';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-user-account',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-account.html',
  styleUrl: './user-account.css'
})
export class UserAccount implements OnInit {
  private readonly employeeApi = inject(EmployeeApiService);
  private readonly userAccountApi = inject(UserAccountService);
  private readonly authService = inject(AuthService);

  employees: EmployeeApiModel[] = [];
  employeeId: number | null = null;
  email = '';
  password = '';
  role = 'EMPLOYEE';
  isLoading = false;
  isLoadingEmployees = false;
  errorMessage = '';
  successMessage = '';

  get canCreateAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  ngOnInit(): void {
    this.isLoadingEmployees = true;
    this.employeeApi.getAll().subscribe({
      next: response => this.employees = Array.isArray(response) ? response : response.data,
      error: error => this.errorMessage = this.getErrorMessage(error),
      complete: () => this.isLoadingEmployees = false
    });
  }

  createAccount(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.employeeId === null || !this.email.trim() || this.password.length < 8) {
      this.errorMessage = 'Select an employee, enter a valid email, and use a password with at least 8 characters.';
      return;
    }

    this.isLoading = true;
    this.userAccountApi.createUser({
      employeeId: this.employeeId,
      email: this.email.trim(),
      password: this.password,
      roles: [this.role]
    }).subscribe({
      next: response => {
        this.successMessage = `${response.data.name}'s account was created successfully.`;
        this.employeeId = null;
        this.email = '';
        this.password = '';
        this.role = 'EMPLOYEE';
      },
      error: error => this.errorMessage = this.getErrorMessage(error),
      complete: () => this.isLoading = false
    });
  }

  private getErrorMessage(error: unknown): string {
    const apiError = error instanceof HttpErrorResponse
      ? error.error as { message?: string; errors?: Record<string, string> } | null
      : null;
    const fieldErrors = apiError?.errors ? Object.values(apiError.errors).join(' ') : '';
    return fieldErrors || apiError?.message || 'Unable to create the user account.';
  }
}