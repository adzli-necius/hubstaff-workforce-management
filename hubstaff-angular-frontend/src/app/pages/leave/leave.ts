import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface LeaveApplication {
  id: number;
  employee: string;
  role: string;
  type: 'Annual Leave' | 'Medical Leave' | 'Emergency Leave';
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
export class Leave {

  search = '';

  selectedStatus = 'All';

  selectedLeave: LeaveApplication | null = null;

  showDetails = false;

  leaveApplications: LeaveApplication[] = [

    {
      id: 1,
      employee: 'Nur Aisyah',
      role: 'Counter Staff',
      type: 'Annual Leave',
      startDate: '10 Sep 2026',
      endDate: '11 Sep 2026',
      days: 2,
      reason: 'Family matters',
      appliedDate: '07 Sep 2026',
      status: 'Pending'
    },

    {
      id: 2,
      employee: 'Muhammad Ali',
      role: 'Delivery Handler',
      type: 'Medical Leave',
      startDate: '08 Sep 2026',
      endDate: '08 Sep 2026',
      days: 1,
      reason: 'Medical appointment',
      appliedDate: '08 Sep 2026',
      status: 'Approved'
    },

    {
      id: 3,
      employee: 'Siti Nur',
      role: 'Counter Staff',
      type: 'Annual Leave',
      startDate: '22 Sep 2026',
      endDate: '23 Sep 2026',
      days: 2,
      reason: 'Personal matters',
      appliedDate: '08 Sep 2026',
      status: 'Pending'
    },

    {
      id: 4,
      employee: 'Daniel Lim',
      role: 'Hub Assistant',
      type: 'Emergency Leave',
      startDate: '04 Sep 2026',
      endDate: '04 Sep 2026',
      days: 1,
      reason: 'Family emergency',
      appliedDate: '04 Sep 2026',
      status: 'Rejected'
    },

    {
      id: 5,
      employee: 'Farah Ahmad',
      role: 'Counter Staff',
      type: 'Annual Leave',
      startDate: '28 Sep 2026',
      endDate: '30 Sep 2026',
      days: 3,
      reason: 'Holiday',
      appliedDate: '01 Sep 2026',
      status: 'Approved'
    }

  ];


  get filteredApplications(): LeaveApplication[] {

    const query = this.search
      .toLowerCase()
      .trim();

    return this.leaveApplications.filter(leave => {

      const matchesSearch =
        !query ||
        leave.employee.toLowerCase().includes(query) ||
        leave.role.toLowerCase().includes(query) ||
        leave.type.toLowerCase().includes(query);

      const matchesStatus =
        this.selectedStatus === 'All' ||
        leave.status === this.selectedStatus;

      return matchesSearch && matchesStatus;

    });
  }


  get pendingCount(): number {
    return this.leaveApplications.filter(
      leave => leave.status === 'Pending'
    ).length;
  }


  get approvedCount(): number {
    return this.leaveApplications.filter(
      leave => leave.status === 'Approved'
    ).length;
  }


  get rejectedCount(): number {
    return this.leaveApplications.filter(
      leave => leave.status === 'Rejected'
    ).length;
  }


  viewDetails(leave: LeaveApplication): void {

    this.selectedLeave = leave;

    this.showDetails = true;

  }


  closeDetails(): void {

    this.selectedLeave = null;

    this.showDetails = false;

  }


  approveLeave(leave: LeaveApplication): void {

    leave.status = 'Approved';

    this.closeDetails();

  }


  rejectLeave(leave: LeaveApplication): void {

    leave.status = 'Rejected';

    this.closeDetails();

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