import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { MaintenanceService } from '../../../core/services/maintenance.service';
import { OrdreMaintenance } from '../../../core/models/maintenance.model';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-meca-ordres',
  templateUrl: './meca-ordres.component.html',
  styleUrls: ['./meca-ordres.component.scss']
})
export class MecaOrdresComponent implements OnInit {
  ordres: OrdreMaintenance[] = [];
  filtered: OrdreMaintenance[] = [];
  filtre = 'TOUS';

  constructor(private maintenanceService: MaintenanceService, public auth: AuthService) {}

  ngOnInit(): void {
    this.maintenanceService.getByTechnicien(this.auth.userId).subscribe({ next: d => { this.ordres = d; this.filtered = d; }, error: () => {} });
  }

  setFiltre(f: string): void {
    this.filtre = f;
    this.filtered = f === 'TOUS' ? this.ordres : this.ordres.filter(o => o.statut === f);
  }

  demarrer(o: OrdreMaintenance): void {
    this.maintenanceService.assigner(o.id, this.auth.userId).subscribe({ next: updated => {
      const i = this.ordres.findIndex(x => x.id === updated.id);
      if (i >= 0) this.ordres[i] = updated;
      this.setFiltre(this.filtre);
    }, error: () => {} });
  }

  cloturer(o: OrdreMaintenance): void {
    this.maintenanceService.cloturer(o.id).subscribe({ next: updated => {
      const i = this.ordres.findIndex(x => x.id === updated.id);
      if (i >= 0) this.ordres[i] = updated;
      this.setFiltre(this.filtre);
    }, error: () => {} });
  }

  getStatutClass(s: string): string {
    const m: Record<string,string> = { SIGNALE:'badge-danger', ASSIGNE:'badge-warning', EN_COURS:'badge-info', RESOLU:'badge-success' };
    return m[s] || 'badge-muted';
  }
  getStatutLabel(s: string): string {
    const m: Record<string,string> = { SIGNALE:'Signalé', ASSIGNE:'Assigné', EN_COURS:'En cours', RESOLU:'Résolu' };
    return m[s] || s;
  }
}


