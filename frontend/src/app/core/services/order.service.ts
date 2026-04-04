import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class OrderService {

  private readonly baseUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) {}

  createOrder(orderId: string): Observable<string> {
    const params = new HttpParams().set('orderId', orderId);
    return this.http.get(`${this.baseUrl}/create`, { params, responseType: 'text' });
  }
}
