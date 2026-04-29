import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vehicule, VehiculeRequest } from '../models/vehicule.model';

@Injectable({ providedIn: 'root' })
export class VehiculeService {
  private apiUrl = 'http://localhost:8081/api/vehicules';
  constructor(private http: HttpClient) {}

  getAll(): Observable<Vehicule[]> { return this.http.get<Vehicule[]>(this.apiUrl); }
  getById(id: number): Observable<Vehicule> { return this.http.get<Vehicule>(`${this.apiUrl}/${id}`); }
  getVoitures(): Observable<Vehicule[]> { return this.http.get<Vehicule[]>(`${this.apiUrl}/voitures`); }
  getCamions(): Observable<Vehicule[]> { return this.http.get<Vehicule[]>(`${this.apiUrl}/camions`); }
  create(data: VehiculeRequest): Observable<Vehicule> { return this.http.post<Vehicule>(this.apiUrl, data); }
  update(id: number, data: VehiculeRequest): Observable<Vehicule> { return this.http.put<Vehicule>(`${this.apiUrl}/${id}`, data); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/${id}`); }
  updateStatut(id: number, statut: string): Observable<Vehicule> {
    return this.http.patch<Vehicule>(`${this.apiUrl}/${id}/statut?statut=${statut}`, {});
  }
  recherche(keyword: string): Observable<Vehicule[]> {
    return this.http.get<Vehicule[]>(`${this.apiUrl}/recherche?keyword=${keyword}`);
  }
}
