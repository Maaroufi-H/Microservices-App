import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule, RouterLink, FormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatInputModule, MatFormFieldModule, MatSelectModule,
    MatProgressSpinnerModule, MatChipsModule, MatSnackBarModule
  ],
  template: `
    <div class="products-page wellness-container">
      <div class="page-header">
        <h1>Nos Produits</h1>
        <p>Solutions médicales pour le confort cervical et le trapèze</p>
      </div>

      <!-- Filtres -->
      <div class="filters">
        <mat-form-field appearance="outline" class="search-field">
          <mat-label>Rechercher un produit</mat-label>
          <input matInput [(ngModel)]="searchQuery" (input)="applyFilters()" placeholder="ex: oreiller, TENS...">
          <mat-icon matPrefix>search</mat-icon>
        </mat-form-field>

        <mat-form-field appearance="outline" class="category-field">
          <mat-label>Catégorie</mat-label>
          <mat-select [(ngModel)]="selectedCategory" (selectionChange)="applyFilters()">
            <mat-option value="">Toutes</mat-option>
            <mat-option value="cervical">Cervical</mat-option>
            <mat-option value="massage">Massage</mat-option>
            <mat-option value="orthopedic">Orthopédique</mat-option>
            <mat-option value="electro">Électrothérapie</mat-option>
            <mat-option value="topique">Topique</mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline" class="sort-field">
          <mat-label>Trier par</mat-label>
          <mat-select [(ngModel)]="sortBy" (selectionChange)="applyFilters()">
            <mat-option value="name">Nom A-Z</mat-option>
            <mat-option value="price-asc">Prix croissant</mat-option>
            <mat-option value="price-desc">Prix décroissant</mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      <!-- Loading -->
      <div class="loading-container" *ngIf="loading">
        <mat-spinner color="primary"></mat-spinner>
        <p>Chargement des produits...</p>
      </div>

      <!-- Products Grid -->
      <div class="products-grid" *ngIf="!loading">
        <mat-card class="product-card" *ngFor="let product of filteredProducts">
          <div class="product-image">
            <mat-icon class="product-icon">medical_services</mat-icon>
            <span class="wellness-badge" *ngIf="product.category">{{ product.category }}</span>
          </div>
          <mat-card-header>
            <mat-card-title class="product-title">{{ product.name }}</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <p class="product-desc">{{ product.description }}</p>
            <div class="product-tags" *ngIf="product.tags">
              <span class="tag" *ngFor="let tag of product.tags?.split(',').slice(0,3)">
                {{ tag.trim() }}
              </span>
            </div>
            <div class="stock-info">
              <mat-icon [class.in-stock]="(product.quantity ?? 0) > 0"
                        [class.out-stock]="(product.quantity ?? 0) === 0">
                {{ (product.quantity ?? 0) > 0 ? 'check_circle' : 'cancel' }}
              </mat-icon>
              <span>{{ (product.quantity ?? 0) > 0 ? 'En stock (' + product.quantity + ')' : 'Rupture de stock' }}</span>
            </div>
          </mat-card-content>
          <mat-card-actions class="card-actions">
            <span class="product-price">{{ product.price | currency:'EUR':'symbol':'1.2-2' }}</span>
            <div class="action-buttons">
              <a mat-icon-button [routerLink]="['/products', product.id]" color="primary">
                <mat-icon>visibility</mat-icon>
              </a>
              <button mat-raised-button class="btn-wellness"
                      [disabled]="(product.quantity ?? 0) === 0"
                      (click)="addToCart(product)">
                <mat-icon>add_shopping_cart</mat-icon>
                Ajouter
              </button>
            </div>
          </mat-card-actions>
        </mat-card>
      </div>

      <!-- Empty state -->
      <div class="empty-state" *ngIf="!loading && filteredProducts.length === 0">
        <mat-icon>search_off</mat-icon>
        <p>Aucun produit trouvé pour votre recherche</p>
        <button mat-button (click)="resetFilters()" class="btn-wellness-outline">
          Réinitialiser les filtres
        </button>
      </div>
    </div>
  `,
  styles: [`
    .products-page { padding: 32px 24px; }
    .page-header { margin-bottom: 32px;
      h1 { font-size: 32px; font-weight: 800; color: var(--w-text); margin: 0 0 8px; }
      p  { color: var(--w-text-muted); margin: 0; }
    }
    .filters { display: flex; gap: 16px; flex-wrap: wrap; margin-bottom: 32px; align-items: flex-start; }
    .search-field   { flex: 2; min-width: 200px; }
    .category-field { flex: 1; min-width: 160px; }
    .sort-field     { flex: 1; min-width: 160px; }
    .loading-container { display: flex; flex-direction: column; align-items: center; gap: 16px; padding: 80px 0; color: var(--w-text-muted); }
    .products-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 24px; }
    .product-card { border-radius: var(--w-radius) !important; border: 1px solid var(--w-border) !important; overflow: hidden; transition: transform 0.2s, box-shadow 0.2s;
      &:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(46,125,50,0.15) !important; }
    }
    .product-image {
      background: linear-gradient(135deg, var(--w-surface), #fff);
      height: 180px; display: flex; align-items: center; justify-content: center; position: relative;
    }
    .product-icon { font-size: 72px !important; color: var(--w-primary); opacity: 0.2; }
    .wellness-badge { position: absolute; top: 12px; left: 12px; }
    .product-title { font-size: 16px; font-weight: 700; color: var(--w-text); }
    .product-desc { font-size: 13px; color: var(--w-text-muted); line-height: 1.5; margin: 8px 0; }
    .product-tags { display: flex; gap: 6px; flex-wrap: wrap; margin: 8px 0; }
    .tag { background: var(--w-surface); color: var(--w-text-muted); font-size: 11px; padding: 2px 8px; border-radius: 12px; border: 1px solid var(--w-border); }
    .stock-info { display: flex; align-items: center; gap: 6px; font-size: 13px; margin-top: 8px;
      mat-icon { font-size: 16px; }
      .in-stock  { color: var(--w-primary); }
      .out-stock { color: #c62828; }
    }
    .card-actions { display: flex; justify-content: space-between; align-items: center; padding: 8px 16px 16px !important; }
    .product-price { font-size: 22px; font-weight: 800; color: var(--w-primary); }
    .action-buttons { display: flex; gap: 8px; align-items: center; }
    .empty-state { text-align: center; padding: 80px 0; color: var(--w-text-muted);
      mat-icon { font-size: 64px !important; opacity: 0.3; }
      p { font-size: 18px; margin: 16px 0 24px; }
    }
  `]
})
export class ProductListComponent implements OnInit {
  allProducts: Product[] = [];
  filteredProducts: Product[] = [];
  loading = true;
  searchQuery = '';
  selectedCategory = '';
  sortBy = 'name';

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.productService.getAll().subscribe({
      next: products => {
        this.allProducts = products;
        this.applyFilters();
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  applyFilters(): void {
    let result = [...this.allProducts];

    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(p =>
        p.name.toLowerCase().includes(q) ||
        p.description?.toLowerCase().includes(q) ||
        p.tags?.toLowerCase().includes(q)
      );
    }

    if (this.selectedCategory) {
      result = result.filter(p => p.category === this.selectedCategory);
    }

    result.sort((a, b) => {
      switch (this.sortBy) {
        case 'price-asc':  return (a.price ?? 0) - (b.price ?? 0);
        case 'price-desc': return (b.price ?? 0) - (a.price ?? 0);
        default:           return a.name.localeCompare(b.name);
      }
    });

    this.filteredProducts = result;
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.selectedCategory = '';
    this.sortBy = 'name';
    this.applyFilters();
  }

  addToCart(product: Product): void {
    this.cartService.addItem(product);
    this.snackBar.open(`${product.name} ajouté au panier !`, 'OK', {
      duration: 2500,
      panelClass: 'snack-success'
    });
  }
}
