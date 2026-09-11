import { Routes } from '@angular/router';

import { Dashboard } from './pages/dashboard/dashboard';
import { Attendance } from './pages/attendance/attendance';
import { Leave } from './pages/leave/leave';
import { Employees } from './pages/employees/employees';

export const routes: Routes = [

  {
    path: 'dashboard',
    component: Dashboard
  },

  {
    path: 'attendance',
    component: Attendance
  },

  {
    path: 'leave',
    component: Leave
  },

  {
    path: 'employees',
    component: Employees
  },

  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },

  {
    path: '**',
    redirectTo: 'dashboard'
  }

];