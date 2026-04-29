export interface User {
  id: number;
  firstname: string;
  lastname: string;
  email: string;
  phone?: string;
  address?: string;
  role: 'CLIENT' | 'ADMIN' | 'SUPER_ADMIN' | 'Technicien';
  actif?: boolean;
  specialite?: string;
  disponible?: boolean;
}

export interface Notification {
  id: number;
  titre: string;
  message: string;
  dateEnvoi: string;
  lue?: boolean;
}

export interface Client extends User {
  permisNumero?: string;
  permisCategorie?: string;
  permisExpiration?: string;
  nombreReservations?: number;
}
