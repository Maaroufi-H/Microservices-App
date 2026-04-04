import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, RouterLink, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatSnackBarModule
  ],
  template: `
    <div class="auth-page">
      <div class="auth-card wellness-card">
        <!-- Header -->
        <div class="auth-header">
          <mat-icon class="auth-logo-icon">healing</mat-icon>
          <h1 class="auth-title">Connexion</h1>
          <p class="auth-subtitle">Bienvenue sur WellnessShop</p>
        </div>

        <!-- Formulaire -->
        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="auth-form">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Email</mat-label>
            <input matInput type="email" formControlName="email" placeholder="votre@email.fr">
            <mat-icon matPrefix>email</mat-icon>
            <mat-error *ngIf="loginForm.get('email')?.hasError('required')">Email requis</mat-error>
            <mat-error *ngIf="loginForm.get('email')?.hasError('email')">Email invalide</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Mot de passe</mat-label>
            <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password">
            <mat-icon matPrefix>lock</mat-icon>
            <button mat-icon-button matSuffix type="button" (click)="hidePassword = !hidePassword">
              <mat-icon>{{ hidePassword ? 'visibility_off' : 'visibility' }}</mat-icon>
            </button>
            <mat-error *ngIf="loginForm.get('password')?.hasError('required')">Mot de passe requis</mat-error>
          </mat-form-field>

          <div class="error-msg" *ngIf="errorMessage">
            <mat-icon>error_outline</mat-icon>
            {{ errorMessage }}
          </div>

          <button mat-raised-button class="btn-wellness submit-btn"
                  type="submit" [disabled]="loginForm.invalid || loading">
            <mat-spinner diameter="20" *ngIf="loading"></mat-spinner>
            <mat-icon *ngIf="!loading">login</mat-icon>
            {{ loading ? 'Connexion...' : 'Se connecter' }}
          </button>
        </form>

        <div class="auth-footer">
          <p>Pas encore de compte ?
            <a routerLink="/register">Créer un compte</a>
          </p>
          <p class="hint-text">
            <mat-icon>info_outline</mat-icon>
            Admin test: admin&#64;wellness.fr / admin123
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-page {
      min-height: calc(100vh - 120px);
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--w-bg);
      padding: 40px 16px;
    }
    .auth-card { width: 100%; max-width: 440px; }
    .auth-header { text-align: center; margin-bottom: 32px; }
    .auth-logo-icon { font-size: 48px !important; color: var(--w-primary); }
    .auth-title { font-size: 28px; font-weight: 800; color: var(--w-text); margin: 8px 0 4px; }
    .auth-subtitle { color: var(--w-text-muted); margin: 0; }
    .auth-form { display: flex; flex-direction: column; gap: 4px; }
    .full-width { width: 100%; }
    .submit-btn { width: 100%; height: 48px; font-size: 16px !important; margin-top: 8px; display: flex; align-items: center; justify-content: center; gap: 8px; }
    .error-msg {
      display: flex; align-items: center; gap: 8px;
      background: #FFEBEE; color: #c62828;
      border-radius: var(--w-radius-sm); padding: 10px 14px;
      font-size: 14px;
      mat-icon { font-size: 18px; }
    }
    .auth-footer { text-align: center; margin-top: 24px; color: var(--w-text-muted); font-size: 14px;
      a { color: var(--w-primary); font-weight: 600; }
    }
    .hint-text { display: flex; align-items: center; justify-content: center; gap: 4px; font-size: 12px; color: #9E9E9E; margin-top: 8px;
      mat-icon { font-size: 14px; }
    }
  `]
})
export class LoginComponent {
  loginForm = this.fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  loading = false;
  hidePassword = true;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  onSubmit(): void {
    if (this.loginForm.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    const { email, password } = this.loginForm.value;
    this.authService.login({ email: email!, password: password! }).subscribe({
      next: res => {
        this.snackBar.open(`Bienvenue ${res.user.name} !`, '✓', { duration: 3000, panelClass: 'snack-success' });
        this.router.navigate(['/products']);
      },
      error: err => {
        this.loading = false;
        this.errorMessage = err.error?.error ?? 'Email ou mot de passe incorrect';
      }
    });
  }
}
