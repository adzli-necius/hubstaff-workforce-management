import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

export const managerGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.hasRole('ADMIN') || authService.hasRole('MANAGER')
    ? true
    : router.createUrlTree(['/dashboard']);
};
