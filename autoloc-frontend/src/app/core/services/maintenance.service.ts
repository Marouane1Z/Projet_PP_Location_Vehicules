import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrdreMaintenance, MaintenanceRequest } from '../models/maintenance.model';

@Injectable({ providedIn: 'root' })
export class MaintenanceService {
  private apiUrl = 'http://localhost:8081/api/maintenance';
  constructor(private http: HttpClient) {}

  getAll(): Observable<OrdreMaintenance[]> { return this.http.get<OrdreMaintenance[]>(this.apiUrl); }
  getById(id: number): Observable<OrdreMaintenance> { return this.http.get<OrdreMaintenance>(`${this.apiUrl}/${id}`); }
  getByTechnicien(techId: number): Observable<OrdreMaintenance[]> {
    return this.http.get<OrdreMaintenance[]>(`${this.apiUrl}/technicien/${techId}`);
  }
  create(data: MaintenanceRequest): Observable<OrdreMaintenance> { return this.http.post<OrdreMaintenance>(this.apiUrl, data); }
  assigner(id: number, technicienId: number): Observable<OrdreMaintenance> {
    return this.http.patch<OrdreMaintenance>(`${this.apiUrl}/${id}/assigner?technicienId=${technicienId}`, {});
  }
  resoudre(id: number, coutReel: number): Observable<OrdreMaintenance> {
    return this.http.patch<OrdreMaintenance>(`${this.apiUrl}/${id}/resoudre?coutReel=${coutReel}`, {});
  }
  cloturer(id: number): Observable<OrdreMaintenance> {
    return this.http.patch<OrdreMaintenance>(`${this.apiUrl}/${id}/cloturer`, {});
  }
}
