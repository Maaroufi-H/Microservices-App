import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { CartItem } from '../../core/models/product.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule, RouterLink,
    MatButtonModule, MatIconModule, MatCardModule,
    MatDividerModule, MatSnackBarModule
  ],
  template: `
    <div class="wellness-container cart-page">
      <h1 class="cart-title">
        <mat-icon>shopping_cart</mat-icon>
        Mon Panier
      </h1>

      <!-- Panier vide -->
      <div class="empty-cart" *ngIf="(cartService.items$ | async)?.length === 0">
        <mat-icon>shopping_cart</mat-icon>
        <h2>Votre panier est vide</h2>
        <p>Découvrez nos produits bien-être pour commencer</p>
        <a mat-raised-button routerLink="/products" class="btn-wellness">
          <mat-icon>storefront</mat-icon>
          Voir les produits
        </a>
      </div>

      <!-- Contenu panier -->
      <div class="cart-layout" *ngIf="(cartService.items$ | async)?.length ?? 0 > 0">
        <!-- Liste articles -->
        <div class="cart-items">
          <mat-card class="cart-item-card" *ngFor="let item of cartService.items$ | async">
            <div class="item-image">
              <mat-icon>medical_services</mat-icon>
            </div>
            <div class="item-info">
              <h3 class="item-name">{{ item.product.name }}</h3>
              <p class="item-desc">{{ item.product.description }}</p>
              <span class="item-unit-price">{{ item.product.price | currency:'EUR':'symbol':'1.2-2' }} / unité</span>
            </div>
            <div class="item-controls">
              <div class="qty-controls">
                <button mat-icon-button (click)="updateQty(item, item.quantity - 1)">
                  <mat-icon>remove</mat-icon>
                </button>
                <span class="qty">{{ item.quantity }}</span>
                <button mat-icon-button (click)="updateQty(item, item.quantity + 1)">
                  <mat-icon>add</mat-icon>
                </button>
              </div>
              <span class="item-total">{{ item.product.price * item.quantity | currency:'EUR':'symbol':'1.2-2' }}</span>
              <button mat-icon-button color="warn" (click)="removeItem(item)">
                <mat-icon>delete_outline</mat-icon>
              </button>
            </div>
          </mat-card>
        </div>

        <!-- Résumé commande -->
        <div class="cart-summary wellness-card">
          <h2 class="summary-title">Résumé de commande</h2>
          <mat-divider></mat-divider>

          <div class="summary-rows">
            <div class="summary-row">
              <span>Sous-total ({{ cartService.itemCount$ | async }} articles)</span>
              <span>{{ cartService.totalPrice$ | async | currency:'EUR':'symbol':'1.2-2' }}</span>
            </div>
            <div class="summary-row">
              <span>Livraison</span>
              <span class="free-shipping">Gratuite</span>
            </div>
          </div>

          <mat-divider></mat-divider>

          <div class="summary-total">
            <span>Total TTC</span>
            <span class="total-amount">{{ cartService.totalPrice$ | async | currency:'EUR':'symbol':'1.2-2' }}</span>
          </div>

          <button mat-raised-button class="btn-wellness order-btn"
                  (click)="placeOrder()" [disabled]="ordering">
            <mat-icon>{{ ordering ? 'hourglass_empty' : 'check_circle' }}</mat-icon>
            {{ ordering ? 'Traitement...' : 'Passer la commande' }}
          </button>

          <a mat-button routerLink="/products" class="continue-shopping">
            <mat-icon>arrow_back</mat-icon>
            Continuer mes achats
          </a>

          <!-- Badges confiance -->
          <div class="trust-badges">
            <div class="trust-badge"><mat-icon>lock</mat-icon><span>Paiement sécurisé</span></div>
            <div class="trust-badge"><mat-icon>local_shipping</mat-icon><span>Livraison 48h</span></div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .cart-page { padding: 32px 24px; }
    .cart-title { display: flex; align-items: center; gap: 12px; font-size: 28px; font-weight: 800; color: var(--w-text); margin-bottom: 32px;
      mat-icon { font-size: 32px; color: var(--w-primary); }
    }
    /* Empty */
    .empty-cart { text-align: center; padding: 80px 0;
      mat-icon { font-size: 80px !important; color: var(--w-border); }
      h2 { color: var(--w-text); margin: 16px 0 8px; }
      p  { color: var(--w-text-muted); margin-bottom: 24px; }
    }
    /* Layout */
    .cart-layout { display: grid; grid-template-columns: 1fr 380px; gap: 32px; align-items: start; }
    /* Items */
    .cart-items { display: flex; flex-direction: column; gap: 16px; }
    .cart-item-card { display: flex !important; align-items: center; gap: 16px; padding: 16px !important; border-radius: var(--w-radius) !important; border: 1px solid var(--w-border) !important; }
    .item-image { width: 80px; height: 80px; background: var(--w-surface); border-radius: var(--w-radius-sm); display: flex; align-items: center; justify-content: center; flex-shrink: 0;
      mat-icon { font-size: 36px !important; color: var(--w-primary); opacity: 0.4; }
    }
    .item-info { flex: 1;
      h3 { font-size: 15px; font-weight: 700; color: var(--w-text); margin: 0 0 4px; }
      p  { font-size: 13px; color: var(--w-text-muted); margin: 0 0 4px; }
      .item-unit-price { font-size: 13px; color: var(--w-text-muted); }
    }
    .item-controls { display: flex; align-items: center; gap: 16px; }
    .qty-controls { display: flex; align-items: center; gap: 8px; border: 1px solid var(--w-border); border-radius: var(--w-radius-sm); padding: 2px 8px; }
    .qty { font-size: 16px; font-weight: 700; min-width: 24px; text-align: center; }
    .item-total { font-size: 18px; font-weight: 800; color: var(--w-primary); min-width: 90px; text-align: right; }
    /* Summary */
    .summary-title { font-size: 20px; font-weight: 700; color: var(--w-text); margin: 0 0 16px; }
    .summary-rows { padding: 16px 0; display: flex; flex-direction: column; gap: 12px; }
    .summary-row { display: flex; justify-content: space-between; font-size: 14px; color: var(--w-text-muted); }
    .free-shipping { color: var(--w-primary); font-weight: 600; }
    .summary-total { display: flex; justify-content: space-between; align-items: center; padding: 16px 0; }
    .total-amount { font-size: 28px; font-weight: 900; color: var(--w-primary); }
    .order-btn { width: 100%; height: 52px !important; font-size: 16px !important; margin: 16px 0 8px; display: flex; align-items: center; justify-content: center; gap: 8px; }
    .continue-shopping { display: flex; align-items: center; justify-content: center; color: var(--w-text-muted) !important; }
    .trust-badges { display: flex; gap: 16px; margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--w-border); }
    .trust-badge { display: flex; align-items: center; gap: 6px; font-size: 12px; color: var(--w-text-muted);
      mat-icon { font-size: 16px; color: var(--w-primary); }
    }
  `]
})
export class CartComponent {
  ordering = false;

  constructor(
    public cartService: CartService,
    private orderService: OrderService,
    private snackBar: MatSnackBar
  ) {}

  updateQty(item: CartItem, qty: number): void {
    this.cartService.updateQuantity(item.product.id, qty);
  }

  removeItem(item: CartItem): void {
    this.cartService.removeItem(item.product.id);
  }

  placeOrder(): void {
    this.ordering = true;
    const orderId = `ORD-${Date.now()}`;
    this.orderService.createOrder(orderId).subscribe({
      next: () => {
        this.cartService.clearCart();
        this.ordering = false;
        this.snackBar.open('Commande passée avec succès ! Référence: ' + orderId, '✓', {
          duration: 5000,
          panelClass: 'snack-success'
        });
      },
      error: () => {
        this.ordering = false;
        this.snackBar.open('Erreur lors de la commande. Réessayez.', 'Fermer', {
          duration: 4000,
          panelClass: 'snack-error'
        });
      }
    });
  }
}
