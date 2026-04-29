export interface Reservation {
  id: number;
  vehiculeId: number;
  vehiculeMarque?: string;
  vehiculeModele?: string;
  immatriculation?: string;
  clientId: number;
  clientNom?: string;
  dateDebut: string;
  dateFin: string;
  dateRetour?: string;
  montant: number;
  caution?: number;
  statut: 'EN_ATTENTE' | 'CONFIRMEE' | 'ANNULEE' | 'REFUSEE' | 'TERMINEE';
}

export interface ReservationRequest {
  vehiculeId: number;
  dateDebut: string;
  dateFin: string;
}
