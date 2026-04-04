import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatStepperModule } from '@angular/material/stepper';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule, RouterLink, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule,
    MatIconModule, MatSelectModule, MatProgressSpinnerModule,
    MatSnackBarModule, MatStepperModule
  ],
  template: `
    <div class="auth-page">
      <div class="auth-card wellness-card">
        <div class="auth-header">
          <mat-icon class="auth-logo-icon">person_add</mat-icon>
          <h1 class="auth-title">Créer un compte</h1>
          <p class="auth-subtitle">Rejoignez la communauté WellnessShop</p>
        </div>

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="auth-form">
          <!-- Ligne nom / prénom -->
          <div class="form-row">
            <mat-form-field appearance="outline">
              <mat-label>Prénom</mat-label>
              <input matInput formControlName="name" placeholder="Jean">
              <mat-icon matPrefix>person</mat-icon>
              <mat-error>Prénom requis</mat-error>
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Nom</mat-label>
              <input matInput formControlName="surname" placeholder="Dupont">
              <mat-icon matPrefix>person</mat-icon>
              <mat-error>Nom requis</mat-error>
            </mat-form-field>
          </div>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Email</mat-label>
            <input matInput type="email" formControlName="email" placeholder="votre@email.fr">
            <mat-icon matPrefix>email</mat-icon>
            <mat-error *ngIf="registerForm.get('email')?.hasError('required')">Email requis</mat-error>
            <mat-error *ngIf="registerForm.get('email')?.hasError('email')">Email invalide</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Mot de passe</mat-label>
            <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password">
            <mat-icon matPrefix>lock</mat-icon>
            <button mat-icon-button matSuffix type="button" (click)="hidePassword = !hidePassword">
              <mat-icon>{{ hidePassword ? 'visibility_off' : 'visibility' }}</mat-icon>
            </button>
            <mat-hint>Minimum 6 caractères</mat-hint>
            <mat-error *ngIf="registerForm.get('password')?.hasError('minlength')">
              6 caractères minimum
            </mat-error>
          </mat-form-field>

          <!-- Ligne âge / genre / pays -->
          <div class="form-row">
            <mat-form-field appearance="outline">
              <mat-label>Âge</mat-label>
              <input matInput type="number" formControlName="age" min="1" max="120">
              <mat-icon matPrefix>cake</mat-icon>
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Genre</mat-label>
              <mat-select formControlName="gender">
                <mat-option value="M">Homme</mat-option>
                <mat-option value="F">Femme</mat-option>
                <mat-option value="other">Autre</mat-option>
              </mat-select>
            </mat-form-field>
          </div>

          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Pays</mat-label>
            <mat-select formControlName="country">
              <mat-option value="FR">France</mat-option>
              <mat-option value="BE">Belgique</mat-option>
              <mat-option value="CH">Suisse</mat-option>
              <mat-option value="MA">Maroc</mat-option>
              <mat-option value="other">Autre</mat-option>
            </mat-select>
            <mat-icon matPrefix>flag</mat-icon>
          </mat-form-field>

          <div class="error-msg" *ngIf="errorMessage">
            <mat-icon>error_outline</mat-icon>
            {{ errorMessage }}
          </div>

          <button mat-raised-button class="btn-wellness submit-btn"
                  type="submit" [disabled]="registerForm.invalid || loading">
            <mat-spinner diameter="20" *ngIf="loading"></mat-spinner>
            <mat-icon *ngIf="!loading">how_to_reg</mat-icon>
            {{ loading ? 'Création...' : 'Créer mon compte' }}
          </button>
        </form>

        <div class="auth-footer">
          <p>Déjà un compte ? <a routerLink="/login">Se connecter</a></p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-page {
      min-height: calc(100vh - 120px);
      display: flex; align-items: center; justify-content: center;
      background: var(--w-bg); padding: 40px 16px;
    }
    .auth-card { width: 100%; max-width: 520px; }
    .auth-header { text-align: center; margin-bottom: 28px; }
    .auth-logo-icon { font-size: 48px !important; color: var(--w-primary); }
    .auth-title { font-size: 28px; font-weight: 800; color: var(--w-text); margin: 8px 0 4px; }
    .auth-subtitle { color: var(--w-text-muted); margin: 0; }
    .auth-form { display: flex; flex-direction: column; gap: 4px; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .full-width { width: 100%; }
    .submit-btn { width: 100%; height: 48px; font-size: 16px !important; margin-top: 8px; display: flex; align-items: center; justify-content: center; gap: 8px; }
    .error-msg {
      display: flex; align-items: center; gap: 8px;
      background: #FFEBEE; color: #c62828;
      border-radius: var(--w-radius-sm); padding: 10px 14px; font-size: 14px;
    }
    .auth-footer { text-align: center; margin-top: 20px; color: var(--w-text-muted); font-size: 14px;
      a { color: var(--w-primary); font-weight: 600; }
    }
  `]
})
export class RegisterComponent {
  registerForm = this.fb.group({
    name:     ['', Validators.required],
    surname:  ['', Validators.required],
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    age:      [25, [Validators.required, Validators.min(1), Validators.max(120)]],
    gender:   [''],
    country:  ['FR']
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
    if (this.registerForm.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    const v = this.registerForm.value;
    this.authService.register({
      name: v.name!, surname: v.surname!, email: v.email!,
      password: v.password!, age: v.age!, gender: v.gender ?? '', country: v.country ?? ''
    }).subscribe({
      next: res => {
        this.snackBar.open(`Bienvenue ${res.user.name} ! Compte créé avec succès.`, '✓', { duration: 4000, panelClass: 'snack-success' });
        this.router.navigate(['/products']);
      },
      error: err => {
        this.loading = false;
        this.errorMessage = err.error?.error ?? 'Erreur lors de la création du compte';
      }
    });
  }
}
