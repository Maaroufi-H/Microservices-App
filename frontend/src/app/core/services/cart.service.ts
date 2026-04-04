import { Injectable } from '@angular/core';
import { BehaviorSubject, map } from 'rxjs';
import { CartItem, Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class CartService {

  private itemsSubject = new BehaviorSubject<CartItem[]>([]);
  items$ = this.itemsSubject.asObservable();

  itemCount$ = this.items$.pipe(
    map(items => items.reduce((sum, item) => sum + item.quantity, 0))
  );

  totalPrice$ = this.items$.pipe(
    map(items => items.reduce((sum, item) => sum + item.product.price * item.quantity, 0))
  );

  get items(): CartItem[] {
    return this.itemsSubject.value;
  }

  addItem(product: Product, quantity = 1): void {
    const current = [...this.itemsSubject.value];
    const existing = current.find(i => i.product.id === product.id);
    if (existing) {
      existing.quantity += quantity;
      this.itemsSubject.next(current);
    } else {
      this.itemsSubject.next([...current, { product, quantity }]);
    }
  }

  removeItem(productId: number): void {
    this.itemsSubject.next(
      this.itemsSubject.value.filter(i => i.product.id !== productId)
    );
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeItem(productId);
      return;
    }
    const current = this.itemsSubject.value.map(item =>
      item.product.id === productId ? { ...item, quantity } : item
    );
    this.itemsSubject.next(current);
  }

  clearCart(): void {
    this.itemsSubject.next([]);
  }
}
