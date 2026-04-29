import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { VehiculeService } from '../../../core/services/vehicule.service';
import { ReservationService } from '../../../core/services/reservation.service';
import { AuthService } from '../../../core/services/auth.service';
import { Vehicule } from '../../../core/models/vehicule.model';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-client-vehicules',
  templateUrl: './client-vehicules.component.html',
  styleUrls: ['./client-vehicules.component.scss']
})
export class ClientVehiculesComponent implements OnInit {
  vehicules: Vehicule[] = [];
  filtered: Vehicule[] = [];
  filtre: 'TOUS' | 'VOITURE' | 'CAMION' = 'TOUS';
  disponibleSeulement = false;
  keyword = '';
  selectedVehicule: Vehicule | null = null;
  showModal = false;
  dateDebut = '';
  dateFin = '';
  reservationOk = false;

  constructor(private vehiculeService: VehiculeService, private reservationService: ReservationService, public auth: AuthService) {}

  ngOnInit(): void {
    this.vehiculeService.getAll().subscribe({ next: (data) => { this.vehicules = data; this.applyFilter(); }, error: () => {} });
  }

  applyFilter(): void {
    this.filtered = this.vehicules.filter(v => {
      if (this.filtre === 'VOITURE' && v.type !== 'VOITURE') return false;
      if (this.filtre === 'CAMION' && v.type !== 'CAMION') return false;
      if (this.disponibleSeulement && v.statut !== 'DISPONIBLE') return false;
      if (this.keyword && !`${v.marque} ${v.modele} ${v.immatriculation}`.toLowerCase().includes(this.keyword.toLowerCase())) return false;
      return true;
    });
  }

  setFiltre(f: 'TOUS' | 'VOITURE' | 'CAMION'): void { this.filtre = f; this.applyFilter(); }

  reserver(v: Vehicule): void { this.selectedVehicule = v; this.showModal = true; this.dateDebut = ''; this.dateFin = ''; }

  confirmerReservation(): void {
    if (!this.selectedVehicule || !this.dateDebut || !this.dateFin) return;
    this.reservationService.create({ vehiculeId: this.selectedVehicule.id, dateDebut: this.dateDebut, dateFin: this.dateFin }).subscribe({
      next: () => { this.showModal = false; this.reservationOk = true; setTimeout(() => this.reservationOk = false, 3000); },
      error: () => {}
    });
  }

  getStatutClass(s: string): string {
    const m: Record<string,string> = { DISPONIBLE:'badge-success', LOUE:'badge-info', EN_MAINTENANCE:'badge-warning', HORS_SERVICE:'badge-danger' };
    return m[s] || 'badge-muted';
  }

  getStatutLabel(s: string): string {
    const m: Record<string,string> = { DISPONIBLE:'Disponible', LOUE:'Loué', EN_MAINTENANCE:'Maintenance', HORS_SERVICE:'Hors service' };
    return m[s] || s;
  }
}


