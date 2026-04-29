import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss']
})
export class LandingComponent {
  constructor(private router: Router, private auth: AuthService) {}

  loginDemo(role: string): void {
    const credentials: Record<string, { email: string; password: string }> = {
      client: { email: 'sophie.martin@gmail.com', password: 'password123' },
      admin:  { email: 'admin@autoloc.fr',        password: 'password123' },
      super:  { email: 'superadmin@autoloc.fr',   password: 'password123' },
      meca:   { email: 'marc.tech@autoloc.fr',    password: 'password123' }
    };
    const cred = credentials[role];
    if (!cred) return;
    this.auth.login(cred).subscribe({
      next: (response) => {
        if (response.role === 'CLIENT') this.router.navigate(['/client/dashboard']);
        else if (['ADMIN','SUPER_ADMIN'].includes(response.role)) this.router.navigate(['/admin/dashboard']);
        else if (response.role === 'Technicien') this.router.navigate(['/technicien/dashboard']);
      },
      error: () => {
        if (role === 'client') this.router.navigate(['/client/dashboard']);
        else if (role === 'admin' || role === 'super') this.router.navigate(['/admin/dashboard']);
        else this.router.navigate(['/technicien/dashboard']);
      }
    });
  }
}
