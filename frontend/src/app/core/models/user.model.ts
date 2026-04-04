export interface UserInfo {
  id: number;
  name: string;
  surname: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  surname: string;
  email: string;
  password: string;
  age: number;
  gender?: string;
  country?: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  user: UserInfo;
}
