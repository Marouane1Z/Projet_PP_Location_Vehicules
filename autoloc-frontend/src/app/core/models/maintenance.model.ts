export interface OrdreMaintenance {
  id: number;
  vehiculeId: number;
  vehiculeMarque?: string;
  vehiculeModele?: string;
  immatriculation?: string;
  technicienId?: number;
  technicienNom?: string;
  typeReparation: string;
  description?: string;
  statut: 'SIGNALE' | 'ASSIGNE' | 'EN_COURS' | 'RESOLU' | 'ABANDONNE';
  dateSignal?: string;
  dateResolution?: string;
  coutReparation?: number;
}

export interface MaintenanceRequest {
  vehiculeId: number;
  typeReparation: string;
  description?: string;
  technicienId?: number;
}
