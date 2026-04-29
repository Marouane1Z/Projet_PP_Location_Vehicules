import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { ReservationService } from '../../../core/services/reservation.service';
import { Reservation } from '../../../core/models/reservation.model';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-admin-reservations',
  templateUrl: './admin-reservations.component.html',
  styleUrls: ['./admin-reservations.component.scss']
})
export class AdminReservationsComponent implements OnInit {
  reservations: Reservation[] = [];
  filtered: Reservation[] = [];
  filtre = 'TOUTES';
  filtres = ['TOUTES','EN_ATTENTE','CONFIRMEE','TERMINEE','REFUSEE','ANNULEE'];

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.reservationService.getAll().subscribe({ next: d => { this.reservations = d; this.applyFilter(); }, error: () => {} });
  }

  applyFilter(): void {
    this.filtered = this.filtre === 'TOUTES' ? this.reservations : this.reservations.filter(r => r.statut === this.filtre);
  }

  confirmer(r: Reservation): void {
    this.reservationService.confirmer(r.id).subscribe({ next: () => r.statut = 'CONFIRMEE', error: () => {} });
  }

  refuser(r: Reservation): void {
    this.reservationService.refuser(r.id).subscribe({ next: () => r.statut = 'REFUSEE', error: () => {} });
  }

  get enAttente(): number { return this.reservations.filter(r => r.statut === 'EN_ATTENTE').length; }

  getStatutClass(s: string): string {
    const m: Record<string,string> = { CONFIRMEE:'badge-success', EN_ATTENTE:'badge-warning', TERMINEE:'badge-muted', REFUSEE:'badge-danger', ANNULEE:'badge-danger' };
    return m[s] || 'badge-muted';
  }

  getStatutLabel(s: string): string {
    const m: Record<string,string> = { CONFIRMEE:'Confirmée', EN_ATTENTE:'En attente', TERMINEE:'Terminée', REFUSEE:'Refusée', ANNULEE:'Annulée' };
    return m[s] || s;
  }
}


