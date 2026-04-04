import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [MatIconModule],
  template: `
    <footer class="footer">
      <div class="wellness-container footer-content">
        <div class="footer-brand">
          <mat-icon class="footer-icon">healing</mat-icon>
          <span class="footer-logo">WellnessShop</span>
        </div>
        <p class="footer-tagline">Solutions médicales pour soulager la douleur cervicale et le trapèze</p>
        <div class="footer-links">
          <span>Certifié CE</span>
          <span>·</span>
          <span>Livraison rapide</span>
          <span>·</span>
          <span>Satisfaction garantie</span>
        </div>
        <p class="footer-copy">© {{ currentYear }} WellnessShop. Tous droits réservés.</p>
      </div>
    </footer>
  `,
  styles: [`
    .footer {
      background: var(--w-text);
      color: rgba(255,255,255,0.85);
      padding: 32px 0 16px;
      margin-top: 40px;
    }
    .footer-content { text-align: center; }
    .footer-brand { display: flex; align-items: center; justify-content: center; gap: 8px; margin-bottom: 8px; }
    .footer-icon { color: #A5D6A7; }
    .footer-logo { font-size: 20px; font-weight: 700; color: white; }
    .footer-tagline { color: rgba(255,255,255,0.7); font-size: 14px; margin: 0 0 12px; }
    .footer-links { display: flex; justify-content: center; gap: 8px; font-size: 13px; color: rgba(255,255,255,0.6); margin-bottom: 16px; }
    .footer-copy { font-size: 12px; color: rgba(255,255,255,0.4); margin: 0; }
  `]
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
}
