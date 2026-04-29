import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-technicien-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './technicien-layout.component.html',
  styleUrls: ['./technicien-layout.component.scss']
})
export class TechnicienLayoutComponent {
  constructor(public auth: AuthService) {}
}
