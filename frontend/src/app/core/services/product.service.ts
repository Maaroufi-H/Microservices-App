import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { PagedProducts, Product } from '../models/product.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {

  private readonly baseUrl = `${environment.apiUrl}/api/products`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Product[]> {
    return this.http.get<PagedProducts>(this.baseUrl).pipe(
      map(res => res._embedded?.products ?? [])
    );
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  getFeatured(): Observable<Product[]> {
    // Retourne les 4 premiers produits comme produits vedettes
    return this.getAll().pipe(
      map(products => products.slice(0, 4))
    );
  }
}
