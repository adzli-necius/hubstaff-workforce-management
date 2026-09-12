import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  LeaveRequestResponseDto,
  LeaveService
} from './leave.service';

interface LeaveApplication {
  id: number;
  employeeId: number;
  employee: string;
  role: string;
  type: string;
  leaveTypeId: number;
  startDate: string;
  endDate: string;
  days: number;
  reason: string;
  appliedDate: string;
  status: 'Pending' | 'Approved' | 'Rejected';
}

@Component({
  selector: 'app-leave',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './leave.html',
  styleUrl: './leave.css'
})
export class Leave implements OnInit {

  search = '';

  selectedStatus = 'All';

  selectedLeave: LeaveApplication | null = null;

  showDetails = false;

  leaveApplications: LeaveApplication[] = [];

  pendingCount = 0;
  approvedCount = 0;
  rejectedCount = 0;
  totalCount = 0;

  constructor(private leaveService: LeaveService) {}

  ngOnInit(): void {
    this.loadLeaveRequests();
    this.loadSummary();
  }

  loadLeaveRequests(): void {

    this.leaveService
      .getAllLeaveRequests()
      .subscribe({
        next: (response) => {

          this.leaveApplications =
            response.data.map(leave =>
              this.mapToLeaveApplication(leave)
            );

        },
        error: (error) => {
          console.error(
            'Failed to load leave requests',
            error
          );
        }
      });
  }

  loadSummary(): void {

    this.leaveService
      .getSummary()
      .subscribe({
        next: (response) => {

          this.pendingCount =
            response.data.pending;

          this.approvedCount =
            response.data.approved;

          this.rejectedCount =
            response.data.rejected;

          this.totalCount =
            response.data.total;
        },
        error: (error) => {
          console.error(
            'Failed to load leave summary',
            error
          );
        }
      });
  }

  mapToLeaveApplication(
    leave: LeaveRequestResponseDto
  ): LeaveApplication {

    return {
      id: leave.id,
      employeeId: leave.employeeId,

      // Temporary display values.
      // We will connect employee API later.
      employee: leave.employeeName,
      role: leave.employeeRole,

      leaveTypeId: leave.leaveTypeId,
      type: leave.leaveTypeName,

      startDate: this.formatDate(leave.startDate),
      endDate: this.formatDate(leave.endDate),

      days: leave.totalDays,

      reason: leave.reason,

      appliedDate: this.formatDateTime(
        leave.appliedAt
      ),

      status: this.formatStatus(
        leave.status
      )
    };
  }

  formatStatus(status: string):
    'Pending' | 'Approved' | 'Rejected' {

    const normalized =
      status.toLowerCase();

    if (normalized === 'approved') {
      return 'Approved';
    }

    if (normalized === 'rejected') {
      return 'Rejected';
    }

    return 'Pending';
  }

  formatDate(date: string): string {

    const value = new Date(date);

    return value.toLocaleDateString(
      'en-GB',
      {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      }
    );
  }

  formatDateTime(date: string): string {

    const value = new Date(date);

    return value.toLocaleDateString(
      'en-GB',
      {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      }
    );
  }

  get filteredApplications(): LeaveApplication[] {

    return this.leaveApplications.filter(
      leave => {

        const query =
          this.search
            .toLowerCase()
            .trim();

        const matchesSearch =
          !query ||
          leave.employee
            .toLowerCase()
            .includes(query) ||
          leave.role
            .toLowerCase()
            .includes(query) ||
          leave.type
            .toLowerCase()
            .includes(query);

        const matchesStatus =
          this.selectedStatus === 'All' ||
          leave.status === this.selectedStatus;

        return matchesSearch &&
          matchesStatus;
      }
    );
  }

  viewDetails(
    leave: LeaveApplication
  ): void {

    this.leaveService
      .getLeaveRequestById(leave.id)
      .subscribe({
        next: (response) => {

          this.selectedLeave =
            this.mapToLeaveApplication(
              response.data
            );

          this.showDetails = true;
        },
        error: (error) => {
          console.error(
            'Failed to load leave request',
            error
          );
        }
      });
  }

  closeDetails(): void {

    this.selectedLeave = null;
    this.showDetails = false;
  }

  approveLeave(
    leave: LeaveApplication
  ): void {

    // Approval API will be integrated later.

    console.log(
      'Approve leave:',
      leave.id
    );
  }

  rejectLeave(
    leave: LeaveApplication
  ): void {

    // Rejection API will be integrated later.

    console.log(
      'Reject leave:',
      leave.id
    );
  }

  getInitials(name: string): string {

    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .slice(0, 2)
      .toUpperCase();
  }
}