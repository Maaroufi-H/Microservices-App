export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  quantity: number;
  category?: string;
  tags?: string;
}

// Spring HATEOAS response
export interface PagedProducts {
  _embedded?: {
    products: Product[];
  };
  page?: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
}

export interface CartItem {
  product: Product;
  quantity: number;
}
