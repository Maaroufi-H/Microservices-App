import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule, RouterLink, RouterLinkActive,
    MatToolbarModule, MatButtonModule, MatIconModule,
    MatBadgeModule, MatMenuModule
  ],
  template: `
    <mat-toolbar class="navbar">
      <div class="navbar-content wellness-container">

        <!-- Logo -->
        <a routerLink="/home" class="navbar-logo">
          <mat-icon class="logo-icon">healing</mat-icon>
          <span class="logo-text">WellnessShop</span>
        </a>

        <!-- Navigation links (desktop) -->
        <nav class="navbar-links">
          <a mat-button routerLink="/home" routerLinkActive="active-link">
            <mat-icon>home</mat-icon> Accueil
          </a>
          <a mat-button routerLink="/products" routerLinkActive="active-link">
            <mat-icon>storefront</mat-icon> Produits
          </a>
        </nav>

        <span class="spacer"></span>

        <!-- Panier -->
        <a mat-icon-button routerLink="/cart" *ngIf="authService.isLoggedIn()"
           [matBadge]="(cartService.itemCount$ | async) || null"
           matBadgeColor="warn" class="cart-btn">
          <mat-icon>shopping_cart</mat-icon>
        </a>

        <!-- Auth buttons -->
        <ng-container *ngIf="!authService.isLoggedIn(); else loggedIn">
          <a mat-button routerLink="/login" class="btn-login">Connexion</a>
          <a mat-raised-button routerLink="/register" class="btn-wellness">Inscription</a>
        </ng-container>

        <ng-template #loggedIn>
          <button mat-button [matMenuTriggerFor]="userMenu" class="user-menu-btn">
            <mat-icon>account_circle</mat-icon>
            <span class="user-name">{{ (authService.currentUser$ | async)?.name }}</span>
            <mat-icon>arrow_drop_down</mat-icon>
          </button>
          <mat-menu #userMenu="matMenu">
            <button mat-menu-item disabled>
              <mat-icon>email</mat-icon>
              <span>{{ (authService.currentUser$ | async)?.email }}</span>
            </button>
            <mat-divider></mat-divider>
            <button mat-menu-item (click)="logout()">
              <mat-icon>logout</mat-icon>
              <span>Déconnexion</span>
            </button>
          </mat-menu>
        </ng-template>

      </div>
    </mat-toolbar>
  `,
  styles: [`
    .navbar {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 1000;
      background: var(--w-primary) !important;
      color: white !important;
      box-shadow: 0 2px 8px rgba(0,0,0,0.2);
      height: 64px;
    }
    .navbar-content {
      display: flex;
      align-items: center;
      width: 100%;
    }
    .navbar-logo {
      display: flex;
      align-items: center;
      gap: 8px;
      color: white;
      text-decoration: none;
      font-weight: 700;
      font-size: 20px;
    }
    .logo-icon { color: #A5D6A7; font-size: 28px; }
    .navbar-links {
      display: flex;
      margin-left: 24px;
      a { color: rgba(255,255,255,0.85) !important;
          &:hover, &.active-link { color: white !important; background: rgba(255,255,255,0.1) !important; }
      }
    }
    .spacer { flex: 1; }
    .cart-btn { color: white !important; margin-right: 8px; }
    .btn-login { color: white !important; }
    .user-menu-btn { color: white !important; }
    .user-name { margin: 0 4px; font-weight: 500; }
  `]
})
export class NavbarComponent {
  constructor(
    public authService: AuthService,
    public cartService: CartService,
    private router: Router
  ) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/home']);
  }
}
