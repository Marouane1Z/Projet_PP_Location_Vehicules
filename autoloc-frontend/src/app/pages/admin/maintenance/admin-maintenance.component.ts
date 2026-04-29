import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MaintenanceService } from '../../../core/services/maintenance.service';
import { UserService } from '../../../core/services/user.service';
import { VehiculeService } from '../../../core/services/vehicule.service';
import { OrdreMaintenance } from '../../../core/models/maintenance.model';
import { User } from '../../../core/models/user.model';
import { Vehicule } from '../../../core/models/vehicule.model';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-admin-maintenance',
  templateUrl: './admin-maintenance.component.html',
  styleUrls: ['./admin-maintenance.component.scss']
})
export class AdminMaintenanceComponent implements OnInit {
  ordres: OrdreMaintenance[] = [];
  techniciens: User[] = [];
  vehicules: Vehicule[] = [];
  showCreateModal = false;
  showAssignModal = false;
  selectedOrdre: OrdreMaintenance | null = null;
  newOrdre = { vehiculeId: 0, typeReparation: '', description: '', technicienId: 0 };
  typesPanne = ['Moteur','Freins','Carrosserie','Électrique','Pneus','Climatisation','Transmission'];

  constructor(private maintenanceService: MaintenanceService, private userService: UserService, private vehiculeService: VehiculeService) {}

  ngOnInit(): void {
    this.maintenanceService.getAll().subscribe({ next: d => this.ordres = d, error: () => {} });
    this.userService.getTechniciensDisponibles().subscribe({ next: d => this.techniciens = d, error: () => {} });
    this.vehiculeService.getAll().subscribe({ next: d => this.vehicules = d, error: () => {} });
  }

  creerOrdre(): void {
    this.maintenanceService.create(this.newOrdre).subscribe({ next: (o) => { this.ordres.push(o); this.showCreateModal = false; }, error: () => {} });
  }

  openAssign(o: OrdreMaintenance): void { this.selectedOrdre = o; this.showAssignModal = true; }

  assigner(techId: number): void {
    if (!this.selectedOrdre) return;
    this.maintenanceService.assigner(this.selectedOrdre.id, techId).subscribe({ next: (o) => {
      const i = this.ordres.findIndex(x => x.id === o.id);
      if (i >= 0) this.ordres[i] = o;
      this.showAssignModal = false;
    }, error: () => {} });
  }

  get signales(): number { return this.ordres.filter(o => o.statut === 'SIGNALE').length; }
  get assignes(): number { return this.ordres.filter(o => o.statut === 'ASSIGNE').length; }
  get enCours(): number { return this.ordres.filter(o => o.statut === 'EN_COURS').length; }
  get resolus(): number { return this.ordres.filter(o => o.statut === 'RESOLU').length; }

  getStatutClass(s: string): string {
    const m: Record<string,string> = { SIGNALE:'badge-danger', ASSIGNE:'badge-warning', EN_COURS:'badge-info', RESOLU:'badge-success', ABANDONNE:'badge-muted' };
    return m[s] || 'badge-muted';
  }
  getStatutLabel(s: string): string {
    const m: Record<string,string> = { SIGNALE:'Signalé', ASSIGNE:'Assigné', EN_COURS:'En cours', RESOLU:'Résolu', ABANDONNE:'Abandonné' };
    return m[s] || s;
  }
}


