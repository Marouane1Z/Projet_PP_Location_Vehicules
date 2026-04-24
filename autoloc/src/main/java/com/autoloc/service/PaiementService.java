package com.autoloc.service;

import com.autoloc.dto.PaiementRequest;
import com.autoloc.dto.PaiementResponse;
import com.autoloc.enums.statutPaiement;
import com.autoloc.enums.statutReservation;
import com.autoloc.model.Paiement;
import com.autoloc.model.Reservation;
import com.autoloc.repository.PaiementRepository;
import com.autoloc.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;

    // ─── EFFECTUER PAIEMENT ──────────────────────────────
    public PaiementResponse effectuerPaiement(Long reservationId,
                                              PaiementRequest paiementRequest) {

        // 1. Vérifier que la réservation existe
        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        // 2. Vérifier que la réservation est CONFIRMEE
        if (reservation.getStatutReservation() != statutReservation.CONFIRMEE) {
            throw new RuntimeException("La réservation doit être confirmée pour effectuer un paiement");
        }

        // 3. Vérifier que la réservation n'est pas déjà payée
        if (paiementRepository.findByReservationId(reservationId).isPresent()) {
            throw new RuntimeException("Cette réservation a déjà été payée");
        }

        // 4. Créer le paiement
        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setMontant(paiementRequest.getMontant());
        paiement.setModePaiement(paiementRequest.getModePaiement());
        paiement.setStatutPaiement(statutPaiement.CONFIRME);
        paiement.setDatePaiement(LocalDate.now());

        // 5. Sauvegarder
        paiementRepository.save(paiement);

        // 6. Retourner le DTO
        return mapToResponse(paiement);
    }

    // ─── CONFIRMER ESPÈCES (Admin) ───────────────────────
    public PaiementResponse confirmerEspeces(Long paiementId) {

        // 1. Vérifier que le paiement existe
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        // 2. Vérifier que le statut est EN_ATTENTE
        if (paiement.getStatutPaiement() != statutPaiement.EN_ATTENTE) {
            throw new RuntimeException("Ce paiement ne peut pas être confirmé");
        }

        // 3. Confirmer
        paiement.setStatutPaiement(statutPaiement.CONFIRME);
        paiement.setDatePaiement(LocalDate.now());
        paiementRepository.save(paiement);

        return mapToResponse(paiement);
    }

    // ─── REMBOURSER (Admin) ──────────────────────────────
    public PaiementResponse rembourser(Long paiementId) {

        // 1. Vérifier que le paiement existe
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        // 2. Vérifier que le statut est CONFIRME
        if (paiement.getStatutPaiement() != statutPaiement.CONFIRME) {
            throw new RuntimeException("Seul un paiement confirmé peut être remboursé");
        }

        // 3. Rembourser
        paiement.setStatutPaiement(statutPaiement.REMBOURSE);
        paiementRepository.save(paiement);

        return mapToResponse(paiement);
    }

    // ─── REMBOURSEMENT AUTOMATIQUE ───────────────────────
    public void rembourserAutomatique(Long reservationId) {

        Optional<Paiement> paiementOpt = paiementRepository
                .findByReservationId(reservationId);

        if (paiementOpt.isEmpty()) {
            return;
        }

        Paiement paiement = paiementOpt.get();

        if (paiement.getStatutPaiement() == statutPaiement.CONFIRME) {
            paiement.setStatutPaiement(statutPaiement.REMBOURSE);
            paiementRepository.save(paiement);
        }
    }

    // ─── RÉCUPÉRER UN PAIEMENT ───────────────────────────
    public PaiementResponse getPaiement(Long paiementId) {

        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        return mapToResponse(paiement);
    }

    // ─── RÉCUPÉRER PAIEMENT PAR RÉSERVATION ──────────────
    public PaiementResponse getPaiementByReservation(Long reservationId) {

        Paiement paiement = paiementRepository
                .findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Aucun paiement pour cette réservation"));

        return mapToResponse(paiement);
    }

    // ─── MAPPER ENTITÉ → DTO ─────────────────────────────
    private PaiementResponse mapToResponse(Paiement paiement) {
        PaiementResponse response = new PaiementResponse();
        response.setId(paiement.getId());
        response.setMontant(paiement.getMontant());
        response.setStatut(paiement.getStatutPaiement());
        response.setDatePaiement(paiement.getDatePaiement());
        response.setModePaiement(paiement.getModePaiement());
        return response;
    }
}