import { CommonModule } from '@angular/common';
import { Component, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

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
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnDestroy {

  currentTime = new Date();

  search = '';

  private timer: ReturnType<typeof setInterval>;

  employees: Employee[] = [
    {
      name: 'Ahmad Hakim',
      role: 'Hub Assistant',
      clockIn: '08:03 AM',
      clockOut: '05:12 PM',
      status: 'Present'
    },
    {
      name: 'Siti Nur',
      role: 'Counter Staff',
      clockIn: '08:11 AM',
      clockOut: '--',
      status: 'Present'
    },
    {
      name: 'Muhammad Ali',
      role: 'Delivery Handler',
      clockIn: '08:27 AM',
      clockOut: '--',
      status: 'Late'
    },
    {
      name: 'Nur Aisyah',
      role: 'Counter Staff',
      clockIn: '--',
      clockOut: '--',
      status: 'On Leave'
    },
    {
      name: 'Daniel Lim',
      role: 'Hub Assistant',
      clockIn: '07:56 AM',
      clockOut: '--',
      status: 'Present'
    }
  ];

  leaveRequests: LeaveRequest[] = [
    {
      name: 'Nur Aisyah',
      type: 'Annual Leave',
      dates: '10 Sep – 11 Sep',
      status: 'Pending'
    },
    {
      name: 'Muhammad Ali',
      type: 'Medical Leave',
      dates: '08 Sep',
      status: 'Approved'
    },
    {
      name: 'Siti Nur',
      type: 'Annual Leave',
      dates: '22 Sep – 23 Sep',
      status: 'Pending'
    }
  ];

  constructor() {
    this.timer = setInterval(() => {
      this.currentTime = new Date();
    }, 1000);
  }

  getInitials(name: string): string {
    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .slice(0, 2)
      .toUpperCase();
  }

  get filteredEmployees(): Employee[] {

    const query = this.search
      .toLowerCase()
      .trim();

    if (!query) {
      return this.employees;
    }

    return this.employees.filter(employee =>
      employee.name.toLowerCase().includes(query) ||
      employee.role.toLowerCase().includes(query) ||
      employee.status.toLowerCase().includes(query)
    );
  }

  get presentCount(): number {
    return this.employees.filter(
      employee =>
        employee.status === 'Present' ||
        employee.status === 'Late'
    ).length;
  }

  get leaveCount(): number {
    return this.employees.filter(
      employee => employee.status === 'On Leave'
    ).length;
  }

  get lateCount(): number {
    return this.employees.filter(
      employee => employee.status === 'Late'
    ).length;
  }

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }
}