import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  email = '';
  password = '';
  showPassword = false;
  rememberMe = false;
  isLoading = false;
  errorMessage = '';

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authService: AuthService
  ) {}

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  login(): void {
    this.errorMessage = '';
    if (!this.email.trim() || !this.password) {
      this.errorMessage = 'Enter your email and password.';
      return;
    }

    this.isLoading = true;
    this.authService.login(this.email.trim(), this.password).subscribe({
      next: () => {
        const defaultUrl = this.authService.hasRole('ADMIN') || this.authService.hasRole('MANAGER')
          ? '/dashboard'
          : '/attendance';
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || defaultUrl;
        this.router.navigateByUrl(returnUrl.startsWith('/') ? returnUrl : '/dashboard');
      },
      error: (error: HttpErrorResponse) => {
        this.isLoading = false;
        this.errorMessage = error.status === 401
          ? 'Invalid email or password.'
          : 'Unable to sign in. Please try again.';
      },
      complete: () => this.isLoading = false
    });
  }

  forgotPassword(): void {
    console.log('Forgot password');
  }
}
