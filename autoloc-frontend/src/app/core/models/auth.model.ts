export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstname: string;
  lastname: string;
  email: string;
  phone?: string;
  password: string;
  categoriePermis: string;
}

export interface AuthResponse {
  token: string;
  role: string;
  userId: number;
  firstname: string;
  lastname: string;
  email: string;
}
