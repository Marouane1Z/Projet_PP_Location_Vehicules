export interface Paiement {
  id: number;
  reservationId: number;
  clientNom?: string;
  vehiculeMarque?: string;
  vehiculeModele?: string;
  montant: number;
  datePaiement: string;
  modePaiement: 'CB' | 'CHEQUE' | 'ESPECE';
  statutPaiement: 'EN_ATTENTE' | 'CONFIRME' | 'ECHOUE' | 'REMBOURSE';
}
