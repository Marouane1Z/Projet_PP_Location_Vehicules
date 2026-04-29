import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';
import { TechnicienGuard } from './core/guards/technicien.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  {
    path: 'home',
    loadComponent: () => import('./pages/public/landing/landing.component').then(m => m.LandingComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/public/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/public/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'client',
    canActivate: [AuthGuard],
    loadComponent: () => import('./layouts/client-layout/client-layout.component').then(m => m.ClientLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./pages/client/dashboard/client-dashboard.component').then(m => m.ClientDashboardComponent) },
      { path: 'vehicules', loadComponent: () => import('./pages/client/vehicules/client-vehicules.component').then(m => m.ClientVehiculesComponent) },
      { path: 'reservations', loadComponent: () => import('./pages/client/reservations/client-reservations.component').then(m => m.ClientReservationsComponent) },
      { path: 'notifications', loadComponent: () => import('./pages/client/notifications/client-notifications.component').then(m => m.ClientNotificationsComponent) },
      { path: 'profil', loadComponent: () => import('./pages/client/profil/client-profil.component').then(m => m.ClientProfilComponent) },
      { path: 'paiements', loadComponent: () => import('./pages/client/paiements/client-paiements.component').then(m => m.ClientPaiementsComponent) }
    ]
  },
  {
    path: 'admin',
    canActivate: [AuthGuard, AdminGuard],
    loadComponent: () => import('./layouts/admin-layout/admin-layout.component').then(m => m.AdminLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./pages/admin/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'vehicules', loadComponent: () => import('./pages/admin/vehicules/admin-vehicules.component').then(m => m.AdminVehiculesComponent) },
      { path: 'clients', loadComponent: () => import('./pages/admin/clients/admin-clients.component').then(m => m.AdminClientsComponent) },
      { path: 'reservations', loadComponent: () => import('./pages/admin/reservations/admin-reservations.component').then(m => m.AdminReservationsComponent) },
      { path: 'paiements', loadComponent: () => import('./pages/admin/paiements/admin-paiements.component').then(m => m.AdminPaiementsComponent) },
      { path: 'maintenance', loadComponent: () => import('./pages/admin/maintenance/admin-maintenance.component').then(m => m.AdminMaintenanceComponent) },
      { path: 'rapports', loadComponent: () => import('./pages/admin/rapports/admin-rapports.component').then(m => m.AdminRapportsComponent) },
      { path: 'administrateurs', loadComponent: () => import('./pages/admin/administrateurs/admin-administrateurs.component').then(m => m.AdminAdministrateursComponent) }
    ]
  },
  {
    path: 'technicien',
    canActivate: [AuthGuard, TechnicienGuard],
    loadComponent: () => import('./layouts/technicien-layout/technicien-layout.component').then(m => m.TechnicienLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./pages/technicien/dashboard/meca-dashboard.component').then(m => m.MecaDashboardComponent) },
      { path: 'ordres', loadComponent: () => import('./pages/technicien/ordres/meca-ordres.component').then(m => m.MecaOrdresComponent) },
      { path: 'profil', loadComponent: () => import('./pages/technicien/profil/meca-profil.component').then(m => m.MecaProfilComponent) }
    ]
  },
  { path: '**', redirectTo: '/home' }
];
