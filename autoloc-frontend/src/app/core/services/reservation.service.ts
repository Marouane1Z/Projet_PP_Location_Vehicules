import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation, ReservationRequest } from '../models/reservation.model';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private apiUrl = 'http://localhost:8081/api/reservations';
  constructor(private http: HttpClient) {}

  getAll(): Observable<Reservation[]> { return this.http.get<Reservation[]>(this.apiUrl); }
  getById(id: number): Observable<Reservation> { return this.http.get<Reservation>(`${this.apiUrl}/${id}`); }
  getByClient(clientId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/client/${clientId}`);
  }
  create(data: ReservationRequest): Observable<Reservation> { return this.http.post<Reservation>(this.apiUrl, data); }
  confirmer(id: number): Observable<Reservation> { return this.http.patch<Reservation>(`${this.apiUrl}/${id}/confirmer`, {}); }
  annuler(id: number): Observable<Reservation> { return this.http.patch<Reservation>(`${this.apiUrl}/${id}/annuler`, {}); }
  refuser(id: number): Observable<Reservation> { return this.http.patch<Reservation>(`${this.apiUrl}/${id}/refuser`, {}); }
}
