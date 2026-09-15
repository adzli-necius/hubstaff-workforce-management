import { Routes } from '@angular/router';

import { Dashboard } from './pages/dashboard/dashboard';
import { Attendance } from './pages/attendance/attendance';
import { Leave } from './pages/leave/leave';
import { LeaveApplication } from './pages/leave-application/leave-application';
import { Employees } from './pages/employees/employees';
import { Login } from './auth/login/login';
import { authGuard } from './auth/auth.guard';
import { managerGuard } from './auth/manager.guard';
import { UserAccount } from './pages/user-account/user-account';
import { Overtime } from './pages/overtime/overtime';

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
    path: 'user-accounts/new',
    component: UserAccount,
    canActivate: [authGuard, managerGuard]
  },

  {
    path: 'overtime',
    component: Overtime,
    canActivate: [authGuard, managerGuard]
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