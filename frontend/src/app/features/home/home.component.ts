import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProductService } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { Product } from '../../core/models/product.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, RouterLink,
    MatButtonModule, MatCardModule, MatIconModule, MatChipsModule, MatSnackBarModule
  ],
  template: `
    <!-- HERO SECTION -->
    <section class="hero">
      <div class="wellness-container hero-content">
        <div class="hero-badge">
          <mat-icon>verified</mat-icon>
          Solutions médicales certifiées
        </div>
        <h1 class="hero-title">
          Libérez-vous de la<br>
          <span class="hero-highlight">douleur cervicale</span>
        </h1>
        <p class="hero-subtitle">
          Découvrez notre gamme de produits médicaux spécialisés pour soulager
          les douleurs du cou et du trapèze. Développés avec des professionnels de santé.
        </p>
        <div class="hero-actions">
          <a mat-raised-button routerLink="/products" class="btn-wellness hero-cta">
            <mat-icon>storefront</mat-icon>
            Voir nos produits
          </a>
          <a mat-stroked-button routerLink="/register" class="btn-wellness-outline hero-cta-secondary"
             *ngIf="!authService.isLoggedIn()">
            Créer un compte gratuit
          </a>
        </div>
        <div class="hero-stats">
          <div class="stat"><strong>98%</strong><span>clients satisfaits</span></div>
          <div class="stat-sep">|</div>
          <div class="stat"><strong>5000+</strong><span>commandes livrées</span></div>
          <div class="stat-sep">|</div>
          <div class="stat"><strong>CE</strong><span>certifié médical</span></div>
        </div>
      </div>
      <div class="hero-visual">
        <div class="hero-icon-container">
          <mat-icon class="hero-main-icon">self_improvement</mat-icon>
          <div class="hero-icon-ring"></div>
        </div>
      </div>
    </section>

    <!-- CATÉGORIES -->
    <section class="categories">
      <div class="wellness-container">
        <h2 class="wellness-section-title">Nos Solutions</h2>
        <p class="wellness-section-subtitle">Une gamme complète pour chaque besoin</p>
        <div class="categories-grid">
          <div class="category-card" *ngFor="let cat of categories">
            <div class="category-icon-wrap">
              <mat-icon>{{ cat.icon }}</mat-icon>
            </div>
            <h3>{{ cat.title }}</h3>
            <p>{{ cat.desc }}</p>
            <a mat-button routerLink="/products" class="cat-link">
              Explorer <mat-icon>arrow_forward</mat-icon>
            </a>
          </div>
        </div>
      </div>
    </section>

    <!-- PRODUITS VEDETTES -->
    <section class="featured" *ngIf="featuredProducts.length > 0">
      <div class="wellness-container">
        <h2 class="wellness-section-title">Produits Vedettes</h2>
        <p class="wellness-section-subtitle">Les plus plébiscités par nos clients</p>
        <div class="products-grid">
          <mat-card class="product-card" *ngFor="let product of featuredProducts">
            <div class="product-image">
              <mat-icon class="product-placeholder-icon">medical_services</mat-icon>
              <span class="wellness-badge">Soulagement garanti</span>
            </div>
            <mat-card-content>
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-footer">
                <span class="product-price">{{ product.price | currency:'EUR':'symbol':'1.2-2' }}</span>
                <button mat-raised-button class="btn-wellness btn-add-cart"
                        (click)="addToCart(product)">
                  <mat-icon>add_shopping_cart</mat-icon>
                </button>
              </div>
            </mat-card-content>
          </mat-card>
        </div>
        <div class="see-all-container">
          <a mat-stroked-button routerLink="/products" class="btn-wellness-outline">
            Voir tous les produits <mat-icon>arrow_forward</mat-icon>
          </a>
        </div>
      </div>
    </section>

    <!-- POURQUOI NOUS CHOISIR -->
    <section class="why-us">
      <div class="wellness-container">
        <h2 class="wellness-section-title">Pourquoi WellnessShop ?</h2>
        <div class="why-grid">
          <div class="why-card" *ngFor="let w of whyUs">
            <mat-icon class="why-icon">{{ w.icon }}</mat-icon>
            <h4>{{ w.title }}</h4>
            <p>{{ w.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- TÉMOIGNAGES -->
    <section class="testimonials">
      <div class="wellness-container">
        <h2 class="wellness-section-title">Ce que disent nos clients</h2>
        <div class="testimonials-grid">
          <div class="testimonial-card wellness-card" *ngFor="let t of testimonials">
            <div class="stars">
              <mat-icon *ngFor="let s of [1,2,3,4,5]">star</mat-icon>
            </div>
            <p class="testimonial-text">"{{ t.text }}"</p>
            <div class="testimonial-author">
              <strong>{{ t.name }}</strong>
              <span>{{ t.location }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  `,
  styles: [`
    /* HERO */
    .hero {
      background: linear-gradient(135deg, var(--w-surface) 0%, var(--w-bg) 100%);
      min-height: 80vh;
      display: flex;
      align-items: center;
      position: relative;
      overflow: hidden;
      padding: 60px 0;
    }
    .hero-content { max-width: 600px; }
    .hero-badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      background: var(--w-primary);
      color: white;
      padding: 6px 16px;
      border-radius: 20px;
      font-size: 13px;
      font-weight: 600;
      margin-bottom: 24px;
      mat-icon { font-size: 16px; width: 16px; height: 16px; }
    }
    .hero-title {
      font-size: 52px;
      font-weight: 800;
      line-height: 1.15;
      color: var(--w-text);
      margin: 0 0 20px;
    }
    .hero-highlight { color: var(--w-primary); }
    .hero-subtitle {
      font-size: 18px;
      color: var(--w-text-muted);
      line-height: 1.6;
      margin-bottom: 32px;
    }
    .hero-actions { display: flex; gap: 16px; flex-wrap: wrap; margin-bottom: 40px; }
    .hero-cta { font-size: 16px !important; padding: 12px 28px !important; }
    .hero-cta-secondary { font-size: 16px !important; padding: 12px 28px !important; color: var(--w-primary) !important; border-color: var(--w-primary) !important; }
    .hero-stats { display: flex; align-items: center; gap: 16px; }
    .stat { display: flex; flex-direction: column; strong { font-size: 24px; color: var(--w-primary); } span { font-size: 13px; color: var(--w-text-muted); } }
    .stat-sep { color: var(--w-border); font-size: 24px; }
    .hero-visual {
      position: absolute;
      right: 10%;
      top: 50%;
      transform: translateY(-50%);
    }
    .hero-icon-container { position: relative; width: 280px; height: 280px; display: flex; align-items: center; justify-content: center; }
    .hero-main-icon { font-size: 120px !important; width: 120px !important; height: 120px !important; color: var(--w-primary); opacity: 0.15; }
    .hero-icon-ring { position: absolute; inset: 0; border: 3px solid var(--w-primary); opacity: 0.1; border-radius: 50%; }

    /* CATEGORIES */
    .categories { padding: 80px 0; background: white; }
    .categories-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
    .category-card {
      background: var(--w-surface);
      border-radius: var(--w-radius);
      padding: 32px 24px;
      text-align: center;
      border: 1px solid var(--w-border);
      transition: transform 0.2s, box-shadow 0.2s;
      &:hover { transform: translateY(-4px); box-shadow: var(--w-shadow); }
      h3 { color: var(--w-text); font-weight: 700; margin: 12px 0 8px; }
      p { color: var(--w-text-muted); font-size: 14px; margin-bottom: 16px; }
    }
    .category-icon-wrap {
      width: 64px; height: 64px;
      background: var(--w-primary);
      border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      margin: 0 auto 16px;
      mat-icon { color: white; font-size: 30px; }
    }
    .cat-link { color: var(--w-primary) !important; font-weight: 600; mat-icon { font-size: 16px; vertical-align: middle; } }

    /* PRODUITS */
    .featured { padding: 80px 0; background: var(--w-bg); }
    .products-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 32px; }
    .product-card { border-radius: var(--w-radius) !important; overflow: hidden; border: 1px solid var(--w-border) !important; }
    .product-image {
      background: var(--w-surface);
      height: 160px;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
    }
    .product-placeholder-icon { font-size: 60px !important; color: var(--w-primary); opacity: 0.3; }
    .wellness-badge { position: absolute; top: 12px; left: 12px; }
    .product-name { font-weight: 700; color: var(--w-text); font-size: 15px; margin: 0 0 6px; }
    .product-desc { font-size: 13px; color: var(--w-text-muted); margin: 0 0 12px; line-height: 1.4; }
    .product-footer { display: flex; align-items: center; justify-content: space-between; }
    .product-price { font-size: 20px; font-weight: 800; color: var(--w-primary); }
    .btn-add-cart { min-width: unset !important; width: 40px !important; height: 40px !important; padding: 0 !important; }
    .see-all-container { text-align: center; }

    /* WHY US */
    .why-us { padding: 80px 0; background: white; }
    .why-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
    .why-card { text-align: center; padding: 24px;
      h4 { color: var(--w-text); font-weight: 700; margin: 12px 0 8px; }
      p { color: var(--w-text-muted); font-size: 14px; }
    }
    .why-icon { font-size: 40px !important; color: var(--w-primary); }

    /* TESTIMONIALS */
    .testimonials { padding: 80px 0; background: var(--w-surface); }
    .testimonials-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
    .testimonial-card { }
    .stars { color: #F9A825; margin-bottom: 12px; display: flex; mat-icon { font-size: 18px; } }
    .testimonial-text { font-style: italic; color: var(--w-text-muted); line-height: 1.6; margin-bottom: 16px; }
    .testimonial-author { display: flex; flex-direction: column; strong { color: var(--w-text); } span { font-size: 13px; color: var(--w-text-muted); } }
  `]
})
export class HomeComponent implements OnInit {

  featuredProducts: Product[] = [];

  categories = [
    { icon: 'personal_injury', title: 'Douleur Cervicale', desc: 'Oreillers, colliers et supports pour le rachis cervical' },
    { icon: 'fitness_center', title: 'Trapèze & Épaules', desc: 'Coussins de massage et appareils TENS ciblés' },
    { icon: 'spa', title: 'Relaxation & Récupération', desc: 'Baumes, huiles et accessoires de récupération musculaire' }
  ];

  whyUs = [
    { icon: 'verified_user', title: 'Certifié CE Médical', desc: 'Tous nos produits répondent aux normes médicales européennes' },
    { icon: 'local_shipping', title: 'Livraison Express', desc: 'Livraison sous 48h en France métropolitaine' },
    { icon: 'support_agent', title: 'Conseil Expert', desc: 'Une équipe de spécialistes disponible 6j/7' },
    { icon: 'replay', title: 'Retour Facile', desc: '30 jours pour changer d\'avis, sans question' }
  ];

  testimonials = [
    { name: 'Marie L.', location: 'Paris', text: 'L\'oreiller cervical a changé ma vie. Plus de réveils douloureux après 3 semaines d\'utilisation.' },
    { name: 'Jean-Paul M.', location: 'Lyon', text: 'L\'appareil TENS est incroyable. J\'avais des tensions au trapèze depuis des années, maintenant disparu.' },
    { name: 'Sophie D.', location: 'Bordeaux', text: 'Excellent service client et produits de qualité. Je recommande à tous mes collègues.' }
  ];

  constructor(
    private productService: ProductService,
    public authService: AuthService,
    private cartService: CartService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.productService.getFeatured().subscribe({
      next: products => this.featuredProducts = products,
      error: () => { /* API non disponible, la section reste vide */ }
    });
  }

  addToCart(product: Product): void {
    this.cartService.addItem(product);
    this.snackBar.open(`${product.name} ajouté au panier`, 'OK', {
      duration: 2500,
      panelClass: 'snack-success'
    });
  }
}
