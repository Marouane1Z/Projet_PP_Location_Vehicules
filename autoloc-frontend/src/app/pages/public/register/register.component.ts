import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  form = { firstname: '', lastname: '', email: '', phone: '', password: '', categoriePermis: 'B' };
  loading = false;
  error = '';
  permisOptions = [
    { value: 'B', label: 'B — Voiture' },
    { value: 'BE', label: 'BE — Voiture + remorque' },
    { value: 'C', label: 'C — Poids lourd' },
    { value: 'C1', label: 'C1 — Poids lourd léger' },
    { value: 'CE', label: 'CE — Poids lourd + remorque' }
  ];

  constructor(private auth: AuthService, private router: Router) {}

  register(): void {
    if (!this.form.firstname || !this.form.lastname || !this.form.email || !this.form.password) {
      this.error = 'Veuillez remplir tous les champs obligatoires.'; return;
    }
    this.loading = true; this.error = '';
    this.auth.register(this.form).subscribe({
      next: () => this.router.navigate(['/client/dashboard']),
      error: () => { this.error = 'Erreur lors de la création du compte.'; this.loading = false; }
    });
  }
}
