import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import {TechnicienGuard} from '../guards/technicien.guard';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.getUserFromStorage());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => this.setSession(response))
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data).pipe(
      tap(response => this.setSession(response))
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  private setSession(response: AuthResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('user', JSON.stringify(response));
    this.currentUserSubject.next(response);
  }

  private getUserFromStorage(): AuthResponse | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  get currentUser(): AuthResponse | null { return this.currentUserSubject.value; }
  get isLoggedIn(): boolean { return !!this.currentUserSubject.value; }
  get role(): string { return this.currentUserSubject.value?.role || ''; }
  get isAdmin(): boolean { return ['ADMIN', 'SUPER_ADMIN'].includes(this.role); }
  get isSuperAdmin(): boolean { return this.role === 'SUPER_ADMIN'; }
  get isClient(): boolean { return this.role === 'CLIENT'; }
  get isTechnicien(): boolean { return this.role === 'Technicien'; }
  get userId(): number { return this.currentUserSubject.value?.userId || 0; }
  get fullName(): string {
    const u = this.currentUserSubject.value;
    if (!u || !u.firstname) return '';
    return `${u.firstname} ${u.lastname}`;
  }
  get initials(): string {
    const u = this.currentUserSubject.value;
    if (!u || !u.firstname || !u.lastname) return '?';
    return `${u.firstname[0]}${u.lastname[0]}`.toUpperCase();
  }
}
