import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ReservationService } from '../../../core/services/reservation.service';
import { Reservation } from '../../../core/models/reservation.model';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-client-dashboard',
  templateUrl: './client-dashboard.component.html',
  styleUrls: ['./client-dashboard.component.scss']
})
export class ClientDashboardComponent implements OnInit {
  reservations: Reservation[] = [];
  stats = { actives: 0, enAttente: 0, totalDepense: 0, notifications: 3 };

  constructor(public auth: AuthService, private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.reservationService.getByClient(this.auth.userId).subscribe({
      next: (data) => {
        this.reservations = data.slice(0, 4);
        this.stats.actives = data.filter(r => ['CONFIRMEE','EN_ATTENTE'].includes(r.statut)).length;
        this.stats.enAttente = data.filter(r => r.statut === 'EN_ATTENTE').length;
        this.stats.totalDepense = data.filter(r => r.statut === 'TERMINEE').reduce((s, r) => s + r.montant, 0);
      },
      error: () => {}
    });
  }

  getStatutClass(statut: string): string {
    const map: Record<string, string> = {
      'CONFIRMEE': 'badge-success', 'EN_ATTENTE': 'badge-warning',
      'TERMINEE': 'badge-muted', 'REFUSEE': 'badge-danger', 'ANNULEE': 'badge-danger'
    };
    return map[statut] || 'badge-muted';
  }

  getStatutLabel(statut: string): string {
    const map: Record<string, string> = {
      'CONFIRMEE': 'Confirmée', 'EN_ATTENTE': 'En attente',
      'TERMINEE': 'Terminée', 'REFUSEE': 'Refusée', 'ANNULEE': 'Annulée'
    };
    return map[statut] || statut;
  }
}


