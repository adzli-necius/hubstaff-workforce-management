import { Routes } from '@angular/router';

import { Dashboard } from './pages/dashboard/dashboard';
import { Attendance } from './pages/attendance/attendance';
import { Leave } from './pages/leave/leave';
import { LeaveApplication } from './pages/leave-application/leave-application';
import { Employees } from './pages/employees/employees';
import { Login } from './auth/login/login';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [

  {
    path: 'login',
    component: Login
  },

  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [authGuard]
  },

  {
    path: 'attendance',
    component: Attendance,
    canActivate: [authGuard]
  },

  {
    path: 'leave/apply',
    component: LeaveApplication,
    canActivate: [authGuard]
  },

  {
    path: 'leave',
    component: Leave,
    canActivate: [authGuard]
  },

  {
    path: 'employees',
    component: Employees,
    canActivate: [authGuard]
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