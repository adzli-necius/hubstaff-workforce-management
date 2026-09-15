import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AttendanceApiService, OvertimeRecord } from '../attendance/attendance-api.service';

@Component({
  selector: 'app-overtime',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './overtime.html',
  styleUrl: './overtime.css'
})
export class Overtime implements OnInit {
  private readonly attendanceApi = inject(AttendanceApiService);

  records: OvertimeRecord[] = [];
  approvedMinutes: Record<number, number> = {};
  managerNotes: Record<number, string> = {};
  isLoading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.loadPending();
  }

  approve(record: OvertimeRecord): void {
    const minutes = this.approvedMinutes[record.id] ?? record.requestedMinutes ?? 0;
    this.attendanceApi.approveOvertime(record.id, minutes, this.managerNotes[record.id] ?? '')
      .subscribe({ next: () => this.loadPending(), error: error => this.errorMessage = this.getMessage(error) });
  }

  reject(record: OvertimeRecord): void {
    this.attendanceApi.rejectOvertime(record.id, this.managerNotes[record.id] ?? '')
      .subscribe({ next: () => this.loadPending(), error: error => this.errorMessage = this.getMessage(error) });
  }

  private loadPending(): void {
    this.isLoading = true;
    this.attendanceApi.getPendingOvertime().subscribe({
      next: response => this.records = response.data,
      error: error => this.errorMessage = this.getMessage(error),
      complete: () => this.isLoading = false
    });
  }

  private getMessage(error: { error?: { message?: string } }): string {
    return error.error?.message ?? 'Unable to load overtime records.';
  }
}