import { Routes } from '@angular/router';

import { Dashboard } from './pages/dashboard/dashboard';
import { Attendance } from './pages/attendance/attendance';
import { Leave } from './pages/leave/leave';
import { LeaveApplication } from './pages/leave-application/leave-application';
import { Employees } from './pages/employees/employees';
import { Login } from './auth/login/login';

export const routes: Routes = [

  {
    path: 'login',
    component: Login
  },

  {
    path: 'dashboard',
    component: Dashboard
  },

  {
    path: 'attendance',
    component: Attendance
  },

  {
    path: 'leave/apply',
    component: LeaveApplication
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
    redirectTo: 'login',
    pathMatch: 'full'
  },

  {
    path: '**',
    redirectTo: 'login'
  }

];