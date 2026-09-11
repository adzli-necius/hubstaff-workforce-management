import { CommonModule } from '@angular/common';
import { Component, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './attendance.html',
  styleUrl: './attendance.css'
})
export class Attendance implements OnDestroy {

  currentTime = new Date();

  clockedIn = false;

  clockInTime: Date | null = null;

  clockOutTime: Date | null = null;

  showClockModal = false;

  pendingClockAction: 'in' | 'out' | null = null;

  private timer: ReturnType<typeof setInterval>;

  constructor() {
    this.timer = setInterval(() => {
      this.currentTime = new Date();
    }, 1000);
  }

  clockAction(): void {
    this.pendingClockAction = this.clockedIn ? 'out' : 'in';

    this.showClockModal = true;
  }

  cancelClockAction(): void {
    this.showClockModal = false;

    this.pendingClockAction = null;
  }

  confirmClockAction(): void {

    const now = new Date();

    if (this.pendingClockAction === 'in') {

      this.clockedIn = true;

      this.clockInTime = now;

      this.clockOutTime = null;

    } else if (this.pendingClockAction === 'out') {

      this.clockedIn = false;

      this.clockOutTime = now;

    }

    this.showClockModal = false;

    this.pendingClockAction = null;
  }

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }
}