import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { VehiculeService } from '../../../core/services/vehicule.service';
import { ReservationService } from '../../../core/services/reservation.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  stats = { vehiculesTotal: 0, vehiculesDisponibles: 0, reservationsAttente: 0, caChiffre: 0, clientsActifs: 0 };
  etatParc = { disponibles: 0, loues: 0, maintenance: 0, horsService: 0 };
  activiteRecente = [
    { icon: '👤', text: 'Nouveau client : Lucas Dubois', time: 'Il y a 12 min' },
    { icon: '📅', text: 'Réservation #8 en attente de validation', time: 'Il y a 28 min' },
    { icon: '💳', text: 'Paiement #3 confirmé — 458€', time: 'Il y a 1h' },
    { icon: '🔧', text: 'Ordre de maintenance assigné à M. Durand', time: 'Il y a 2h' },
    { icon: '✅', text: 'Réservation #5 validée', time: 'Il y a 3h' }
  ];

  constructor(private vehiculeService: VehiculeService, public auth: AuthService) {}

  ngOnInit(): void {
    this.vehiculeService.getAll().subscribe({
      next: (vehicules) => {
        this.stats.vehiculesTotal = vehicules.length;
        this.etatParc.disponibles = vehicules.filter(v => v.statut === 'DISPONIBLE').length;
        this.etatParc.loues = vehicules.filter(v => v.statut === 'LOUE').length;
        this.etatParc.maintenance = vehicules.filter(v => v.statut === 'EN_MAINTENANCE').length;
        this.etatParc.horsService = vehicules.filter(v => v.statut === 'HORS_SERVICE').length;
        this.stats.vehiculesDisponibles = this.etatParc.disponibles;
      },
      error: () => {}
    });
  }
}


