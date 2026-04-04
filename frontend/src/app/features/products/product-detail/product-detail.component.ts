import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule, RouterLink,
    MatButtonModule, MatIconModule, MatCardModule,
    MatProgressSpinnerModule, MatSnackBarModule, MatDividerModule
  ],
  template: `
    <div class="wellness-container product-detail-page" *ngIf="product">
      <a mat-button routerLink="/products" class="back-btn">
        <mat-icon>arrow_back</mat-icon> Retour aux produits
      </a>

      <div class="detail-layout">
        <!-- Image -->
        <div class="detail-image">
          <mat-icon class="detail-product-icon">medical_services</mat-icon>
          <span class="wellness-badge" *ngIf="product.category">{{ product.category }}</span>
        </div>

        <!-- Infos -->
        <div class="detail-info">
          <h1 class="detail-title">{{ product.name }}</h1>
          <p class="detail-description">{{ product.description }}</p>

          <div class="detail-tags" *ngIf="product.tags">
            <span class="tag" *ngFor="let tag of product.tags.split(',')">{{ tag.trim() }}</span>
          </div>

          <mat-divider class="divider"></mat-divider>

          <div class="detail-price">
            {{ product.price | currency:'EUR':'symbol':'1.2-2' }}
          </div>

          <div class="detail-stock">
            <mat-icon [class.in-stock]="(product.quantity ?? 0) > 0">
              {{ (product.quantity ?? 0) > 0 ? 'check_circle' : 'cancel' }}
            </mat-icon>
            <span>{{ (product.quantity ?? 0) > 0 ? 'En stock — ' + product.quantity + ' disponibles' : 'Rupture de stock' }}</span>
          </div>

          <div class="qty-row">
            <button mat-icon-button (click)="qty > 1 && qty--" class="qty-btn"><mat-icon>remove</mat-icon></button>
            <span class="qty-value">{{ qty }}</span>
            <button mat-icon-button (click)="qty++" class="qty-btn"><mat-icon>add</mat-icon></button>
          </div>

          <div class="detail-actions">
            <button mat-raised-button class="btn-wellness add-cart-btn"
                    [disabled]="(product.quantity ?? 0) === 0"
                    (click)="addToCart()">
              <mat-icon>add_shopping_cart</mat-icon>
              Ajouter au panier
            </button>
            <a mat-stroked-button routerLink="/cart" class="btn-wellness-outline">
              <mat-icon>shopping_cart</mat-icon> Mon panier
            </a>
          </div>

          <!-- Garanties -->
          <div class="guarantees">
            <div class="guarantee" *ngFor="let g of guarantees">
              <mat-icon>{{ g.icon }}</mat-icon>
              <span>{{ g.text }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="loading-container" *ngIf="loading">
      <mat-spinner></mat-spinner>
    </div>
  `,
  styles: [`
    .product-detail-page { padding: 24px; }
    .back-btn { color: var(--w-primary) !important; margin-bottom: 24px; }
    .detail-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 48px; align-items: start; }
    .detail-image {
      background: linear-gradient(135deg, var(--w-surface), #fff);
      border-radius: var(--w-radius);
      border: 1px solid var(--w-border);
      height: 400px;
      display: flex; align-items: center; justify-content: center;
      position: relative;
    }
    .detail-product-icon { font-size: 120px !important; color: var(--w-primary); opacity: 0.2; }
    .wellness-badge { position: absolute; top: 16px; left: 16px; }
    .detail-title { font-size: 28px; font-weight: 800; color: var(--w-text); margin: 0 0 12px; }
    .detail-description { color: var(--w-text-muted); line-height: 1.7; font-size: 15px; }
    .detail-tags { display: flex; gap: 8px; flex-wrap: wrap; margin: 16px 0; }
    .tag { background: var(--w-surface); color: var(--w-text-muted); font-size: 12px; padding: 3px 10px; border-radius: 12px; border: 1px solid var(--w-border); }
    .divider { margin: 20px 0; }
    .detail-price { font-size: 36px; font-weight: 900; color: var(--w-primary); margin: 12px 0; }
    .detail-stock { display: flex; align-items: center; gap: 8px; font-size: 14px; margin-bottom: 16px;
      .in-stock { color: var(--w-primary); }
    }
    .qty-row { display: flex; align-items: center; gap: 16px; margin-bottom: 20px;
      .qty-btn { border: 1px solid var(--w-border) !important; }
      .qty-value { font-size: 20px; font-weight: 700; min-width: 32px; text-align: center; }
    }
    .detail-actions { display: flex; gap: 16px; margin-bottom: 24px; flex-wrap: wrap; }
    .add-cart-btn { height: 48px !important; font-size: 15px !important; flex: 1; }
    .guarantees { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; padding-top: 16px; border-top: 1px solid var(--w-border); }
    .guarantee { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--w-text-muted);
      mat-icon { color: var(--w-primary); font-size: 18px; }
    }
    .loading-container { display: flex; justify-content: center; padding: 80px; }
  `]
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = true;
  qty = 1;

  guarantees = [
    { icon: 'verified_user', text: 'Certifié CE médical' },
    { icon: 'local_shipping', text: 'Livraison 48h' },
    { icon: 'replay',         text: 'Retour 30 jours' },
    { icon: 'security',       text: 'Paiement sécurisé' }
  ];

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getById(id).subscribe({
      next: p => { this.product = p; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  addToCart(): void {
    if (!this.product) return;
    this.cartService.addItem(this.product, this.qty);
    this.snackBar.open(`${this.product.name} ajouté (x${this.qty})`, 'OK', {
      duration: 2500,
      panelClass: 'snack-success'
    });
  }
}
