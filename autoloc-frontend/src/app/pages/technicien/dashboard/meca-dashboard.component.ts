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
  selector: 'app-meca-dashboard',
  templateUrl: './meca-dashboard.component.html',
  styleUrls: ['./meca-dashboard.component.scss']
})
export class MecaDashboardComponent implements OnInit {
  ordres: OrdreMaintenance[] = [];
  ordresEnCours: OrdreMaintenance[] = [];
  today = new Date().toLocaleDateString('fr-FR', { weekday:'long', day:'numeric', month:'long' });

  constructor(public auth: AuthService, private maintenanceService: MaintenanceService) {}

  ngOnInit(): void {
    this.maintenanceService.getByTechnicien(this.auth.userId).subscribe({
      next: (data) => { this.ordres = data; this.ordresEnCours = data.filter(o => o.statut === 'EN_COURS'); },
      error: () => {}
    });
  }

  get actifs(): number { return this.ordres.filter(o => ['ASSIGNE','EN_COURS'].includes(o.statut)).length; }
  get resolus(): number { return this.ordres.filter(o => o.statut === 'RESOLU').length; }

  getStatutClass(s: string): string {
    const m: Record<string,string> = { SIGNALE:'badge-danger', ASSIGNE:'badge-warning', EN_COURS:'badge-info', RESOLU:'badge-success' };
    return m[s] || 'badge-muted';
  }
  getStatutLabel(s: string): string {
    const m: Record<string,string> = { SIGNALE:'Signalé', ASSIGNE:'Assigné', EN_COURS:'En cours', RESOLU:'Résolu' };
    return m[s] || s;
  }
}


