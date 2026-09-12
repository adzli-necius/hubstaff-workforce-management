import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { EmployeeApiModel, EmployeeApiService, EmployeeRequest, EmploymentStatus } from './employee-api.service';

interface Employee {
  id: number;
  employeeId: string;
  firstName: string;
  lastName: string;
  name: string;
  role: string;
  phone: string;
  joinDate: string;
  status: 'Active' | 'Inactive';
}

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employees.html',
  styleUrl: './employees.css'
})
export class Employees implements OnInit {

  private readonly employeeApi = inject(EmployeeApiService);

  search = '';
  selectedRole = 'All';
  selectedStatus = 'All';

  selectedEmployee: Employee | null = null;

  showDetails = false;
  showForm = false;

  isEditing = false;
  isLoading = false;
  errorMessage = '';
  notificationMessage = '';
  notificationType: 'success' | 'error' = 'success';

  private notificationTimeout?: ReturnType<typeof setTimeout>;

  formEmployee: Employee = this.emptyEmployee();

  employees: Employee[] = [];

  ngOnInit(): void {
    this.loadEmployees();
  }

  get filteredEmployees(): Employee[] {
    const query = this.search.toLowerCase().trim();

    return this.employees.filter(employee => {

      const matchesSearch =
        !query ||
        employee.name.toLowerCase().includes(query) ||
        employee.employeeId.toLowerCase().includes(query) ||
        employee.role.toLowerCase().includes(query) ||
        employee.phone.toLowerCase().includes(query);

      const matchesRole =
        this.selectedRole === 'All' ||
        employee.role === this.selectedRole;

      const matchesStatus =
        this.selectedStatus === 'All' ||
        employee.status === this.selectedStatus;

      return matchesSearch && matchesRole && matchesStatus;
    });
  }

  get activeCount(): number {
    return this.employees.filter(
      employee => employee.status === 'Active'
    ).length;
  }

  get inactiveCount(): number {
    return this.employees.filter(
      employee => employee.status === 'Inactive'
    ).length;
  }

  get totalCount(): number {
    return this.employees.length;
  }

  viewEmployee(employee: Employee): void {
    this.selectedEmployee = employee;
    this.showDetails = true;
  }

  closeDetails(): void {
    this.selectedEmployee = null;
    this.showDetails = false;
  }

  openAddForm(): void {
    this.isEditing = false;
    this.formEmployee = this.emptyEmployee();
    this.showForm = true;
  }

  openEditForm(employee: Employee): void {
    this.isEditing = true;
    this.formEmployee = { ...employee };
    this.showForm = true;
    this.closeDetails();
  }

  closeForm(): void {
    this.showForm = false;
    this.formEmployee = this.emptyEmployee();
  }

  saveEmployee(): void {

    if (!this.formEmployee.firstName.trim() ||
        !this.formEmployee.lastName.trim() ||
        !this.formEmployee.employeeId.trim() ||
        !this.formEmployee.role.trim()) {
      this.showNotification('error', 'First name, last name, employee ID, and role are required.');
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    const request = this.toRequest(this.formEmployee);
    const action = this.isEditing ? 'updated' : 'created';
    const operation = this.isEditing
      ? this.employeeApi.update(this.formEmployee.id, request)
      : this.employeeApi.create(request);

    operation.subscribe({
      next: () => {
        this.closeForm();
        this.showNotification('success', `Employee ${action} successfully.`);
        this.loadEmployees();
      },
      error: error => this.handleSaveError(error),
      complete: () => this.isLoading = false
    });
  }

  deleteEmployee(employee: Employee): void {

    const confirmed = window.confirm(
      `Are you sure you want to delete ${employee.name}?`
    );

    if (!confirmed) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.employeeApi.delete(employee.id).subscribe({
      next: () => {
        this.closeDetails();
        this.loadEmployees();
      },
      error: error => this.handleError(error),
      complete: () => this.isLoading = false
    });
  }

  getInitials(name: string): string {
    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .slice(0, 2)
      .toUpperCase();
  }

  private loadEmployees(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.employeeApi.getAll().subscribe({
      next: response => {
        // Supports the current standard API envelope and a backend instance that
        // was started before the envelope was introduced.
        const apiEmployees = Array.isArray(response) ? response : response.data;

        if (!Array.isArray(apiEmployees)) {
          this.employees = [];
          this.errorMessage = 'Employee service returned an invalid list response.';
          return;
        }

        this.employees = apiEmployees.map(employee => this.toViewModel(employee));
      },
      error: error => this.handleError(error),
      complete: () => this.isLoading = false
    });
  }

  private toViewModel(employee: EmployeeApiModel): Employee {
    return {
      id: employee.id,
      employeeId: employee.employeeCode,
      firstName: employee.firstName,
      lastName: employee.lastName,
      name: [employee.firstName, employee.lastName].filter(Boolean).join(' '),
      role: employee.role,
      phone: employee.phone ?? '',
      joinDate: employee.hireDate,
      status: employee.employmentStatus.toLowerCase() === 'inactive' ? 'Inactive' : 'Active'
    };
  }

  private toRequest(employee: Employee): EmployeeRequest {
    return {
      employeeCode: employee.employeeId.trim(),
      firstName: employee.firstName.trim(),
      lastName: employee.lastName.trim(),
      role: employee.role,
      phone: employee.phone.trim(),
      managerId: null,
      employmentStatus: employee.status.toLowerCase() as EmploymentStatus,
      hireDate: employee.joinDate
    };
  }

  private handleError(error: HttpErrorResponse): void {
    this.isLoading = false;
    this.errorMessage = this.getErrorMessage(error);
  }

  private handleSaveError(error: HttpErrorResponse): void {
    this.isLoading = false;
    this.showNotification('error', this.getErrorMessage(error));
  }

  private getErrorMessage(error: HttpErrorResponse): string {
    const apiError = error.error as { message?: string; errors?: Record<string, string> } | null;
    const fieldErrors = apiError?.errors ? Object.values(apiError.errors).join(' ') : '';
    return fieldErrors || apiError?.message || 'Unable to communicate with the employee service.';
  }

  private showNotification(type: 'success' | 'error', message: string): void {
    this.notificationType = type;
    this.notificationMessage = message;
    clearTimeout(this.notificationTimeout);
    this.notificationTimeout = setTimeout(() => this.notificationMessage = '', 4000);
  }

  private emptyEmployee(): Employee {
    return {
      id: 0,
      employeeId: '',
      firstName: '',
      lastName: '',
      name: '',
      role: 'Hub Assistant',
      phone: '',
      joinDate: '',
      status: 'Active'
    };
  }
}
