import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive],
  selector: 'app-admin-administrateurs',
  templateUrl: './admin-administrateurs.component.html',
  styleUrls: ['./admin-administrateurs.component.scss']
})
export class AdminAdministrateursComponent {
  showModal = false;
  admins = [
    { id: 1, firstname: 'Pierre', lastname: 'Leclerc', email: 'admin@autoloc.fr', niveau: 'Admin Standard', actif: true, creeLe: '01/01/2025' },
    { id: 2, firstname: 'Marie', lastname: 'Fontaine', email: 'marie.f@autoloc.fr', niveau: 'Admin Standard', actif: true, creeLe: '15/02/2025' },
    { id: 3, firstname: 'SUPER', lastname: 'ADMIN', email: 'superadmin@autoloc.fr', niveau: 'Super Admin', actif: true, creeLe: '01/01/2025' }
  ];
  newAdmin = { firstname: '', lastname: '', email: '', password: '', niveau: 'ADMIN' };

  creer(): void { this.showModal = false; }
}


