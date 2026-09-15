import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from './auth/auth.service';

interface Employee {
  name: string;
  role: string;
  clockIn: string;
  clockOut: string;
  status: 'Present' | 'Late' | 'On Leave';
}

interface LeaveRequest {
  name: string;
  type: string;
  dates: string;
  status: 'Pending' | 'Approved' | 'Rejected';
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    RouterModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  activePage = 'Dashboard';
  clockedIn = false;
  currentTime = new Date();
  search = '';

  getInitials(name: string): string {
  return name
    .split(' ')
    .map(word => word.charAt(0))
    .join('')
    .slice(0, 2)
    .toUpperCase();
}

  employees: Employee[] = [
    { name: 'Ahmad Hakim', role: 'Hub Assistant', clockIn: '08:03 AM', clockOut: '05:12 PM', status: 'Present' },
    { name: 'Siti Nur', role: 'Counter Staff', clockIn: '08:11 AM', clockOut: '--', status: 'Present' },
    { name: 'Muhammad Ali', role: 'Delivery Handler', clockIn: '08:27 AM', clockOut: '--', status: 'Late' },
    { name: 'Nur Aisyah', role: 'Counter Staff', clockIn: '--', clockOut: '--', status: 'On Leave' },
    { name: 'Daniel Lim', role: 'Hub Assistant', clockIn: '07:56 AM', clockOut: '--', status: 'Present' }
  ];

  leaveRequests: LeaveRequest[] = [
    { name: 'Nur Aisyah', type: 'Annual Leave', dates: '10 Sep – 11 Sep', status: 'Pending' },
    { name: 'Muhammad Ali', type: 'Medical Leave', dates: '08 Sep', status: 'Approved' },
    { name: 'Siti Nur', type: 'Annual Leave', dates: '22 Sep – 23 Sep', status: 'Pending' }
  ];

  constructor(
    public router: Router,
    private readonly authService: AuthService
  ) {
    setInterval(() => this.currentTime = new Date(), 1000);
  }

  get canManageUsers(): boolean {
    return this.authService.hasRole('ADMIN') || this.authService.hasRole('MANAGER');
  }

  get currentUserName(): string {
    return this.authService.getCurrentUser()?.name ?? 'Employee';
  }

  get currentUserRole(): string {
    const role = this.authService.getCurrentUser()?.roles[0];
    return role ? role.charAt(0) + role.slice(1).toLowerCase() : 'Employee';
  }

  setPage(page: string) {
    this.activePage = page;
  }

  clockAction() {
    this.clockedIn = !this.clockedIn;
  }

  get filteredEmployees() {
    const q = this.search.toLowerCase().trim();
    if (!q) return this.employees;
    return this.employees.filter(e =>
      e.name.toLowerCase().includes(q) ||
      e.role.toLowerCase().includes(q) ||
      e.status.toLowerCase().includes(q)
    );
  }

  get presentCount() {
    return this.employees.filter(e => e.status === 'Present' || e.status === 'Late').length;
  }

  get leaveCount() {
    return this.employees.filter(e => e.status === 'On Leave').length;
  }

  get lateCount() {
    return this.employees.filter(e => e.status === 'Late').length;
  }
}