import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email = '';
  password = '';
  loading = false;
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  login(): void {
    if (!this.email || !this.password) { this.error = 'Veuillez remplir tous les champs.'; return; }
    this.loading = true; this.error = '';
    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        if (res.role === 'CLIENT') this.router.navigate(['/client/dashboard']);
        else if (['ADMIN','SUPER_ADMIN'].includes(res.role)) this.router.navigate(['/admin/dashboard']);
        else if (res.role === 'Technicien') this.router.navigate(['/mecanicien/dashboard']);
      },
      error: () => { this.error = 'Email ou mot de passe incorrect.'; this.loading = false; }
    });
  }

  loginDemo(role: string): void {
    const map: Record<string, {email:string;password:string}> = {
      client: {email:'sophie.martin@gmail.com',password:'password123'},
      admin:  {email:'admin@autoloc.fr',password:'password123'},
      super:  {email:'superadmin@autoloc.fr',password:'password123'},
      meca:   {email:'marc.tech@autoloc.fr',password:'password123'}
    };
    const cred = map[role];
    if (cred) { this.email = cred.email; this.password = cred.password; this.login(); }
  }
}
