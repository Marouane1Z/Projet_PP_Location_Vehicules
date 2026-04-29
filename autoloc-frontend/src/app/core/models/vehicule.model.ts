export interface Vehicule {
  id: number;
  marque: string;
  modele: string;
  immatriculation: string;
  annee: number;
  prixParJour: number;
  caution: number;
  statut: 'DISPONIBLE' | 'LOUE' | 'EN_MAINTENANCE' | 'HORS_SERVICE';
  type: 'VOITURE' | 'CAMION';
  typeCarburant?: string;
  typeBoite?: string;
  image?: string;
  nbPortes?: number;
  nbPlaces?: number;
  categorie?: string;
  tonnage?: number;
  volume?: number;
  longueur?: number;
  elevator?: boolean;
  options?: Option[];
}

export interface Option {
  id: number;
  nom: string;
}

export interface VehiculeRequest {
  type: string;
  marque: string;
  modele: string;
  immatriculation: string;
  annee: number;
  prixParJour: number;
  caution: number;
  statut?: string;
  typeCarburant?: string;
  typeBoite?: string;
  nbPortes?: number;
  nbPlaces?: number;
  categorie?: string;
  tonnage?: number;
  volume?: number;
}
