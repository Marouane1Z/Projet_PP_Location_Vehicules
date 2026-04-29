import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private apiUrl = 'http://localhost:8081/api/notifications';
  constructor(private http: HttpClient) {}

  getByUser(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/user/${userId}`);
  }
  marquerLue(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/lue`, {});
  }
}
