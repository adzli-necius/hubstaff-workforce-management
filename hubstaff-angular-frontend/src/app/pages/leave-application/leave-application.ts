import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface LeaveForm {
  leaveType: string;
  startDate: string;
  endDate: string;
  reason: string;
}

@Component({
  selector: 'app-leave-application',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './leave-application.html',
  styleUrl: './leave-application.css'
})
export class LeaveApplication {
  form: LeaveForm = {
    leaveType: '',
    startDate: '',
    endDate: '',
    reason: ''
  };

  submitted = false;
  submittedMessage = '';

  get minimumDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  submitApplication(): void {
    this.submitted = true;
    this.submittedMessage = 'Your leave application has been submitted for review.';
  }

  resetApplication(): void {
    this.form = {
      leaveType: '',
      startDate: '',
      endDate: '',
      reason: ''
    };
    this.submitted = false;
    this.submittedMessage = '';
  }
}