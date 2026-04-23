package com.autoloc.service;

import com.autoloc.dto.ReservationRequest;
import com.autoloc.dto.ReservationResponse;
import com.autoloc.enums.statutReservation;
import com.autoloc.model.Client;
import com.autoloc.model.Reservation;
import com.autoloc.model.Vehicule;
import com.autoloc.repository.ClientRepository;
import com.autoloc.repository.ReservationRepository;
import com.autoloc.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ClientRepository      clientRepository;
    private final VehiculeRepository    vehiculeRepository;

    // CRÉER UNE RÉSERVATION
    public ReservationResponse createReservation(Long clientId, ReservationRequest request) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        boolean exists = reservationRepository.existsByVehiculeIdAndDateRange(
                vehicule.getId(),
                request.getDateDebut(),
                request.getDateFin()
        );
        if (exists) {
            throw new RuntimeException("Véhicule déjà réservé sur cette période");
        }

        // Mapping DTO → Entity
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());
        reservation.setDateCreation(LocalDate.now());
        reservation.setStatutReservation(statutReservation.EN_ATTENTE);

        long days = ChronoUnit.DAYS.between(request.getDateDebut(), request.getDateFin());
        double montant = days * vehicule.getPrixParJour();
        reservation.setMontant(montant);

        return mapToResponse(reservationRepository.save(reservation));
    }

    // annuler()appelée depuis ClientService.annulerReservations()
    public void annuler(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        if (reservation.getStatutReservation() == statutReservation.EN_ATTENTE ||
                reservation.getStatutReservation() == statutReservation.TERMINEE) {
            throw new RuntimeException("Impossible d'annuler une réservation " +
                    reservation.getStatutReservation());
        }

        reservation.setStatutReservation(statutReservation.ANNULEE);
        reservationRepository.save(reservation);
    }

    // modifier() appelée depuis ClientService.modifierReservations()
    public ReservationResponse modifier(Long reservationId, ReservationRequest request) {        // On ne peut annuler que si EN_ATTENTE ou CONFIRMEE


        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        if (reservation.getStatutReservation() != statutReservation.EN_ATTENTE) {
            throw new RuntimeException("Seule une réservation EN_ATTENTE peut être modifiée");
        }

        boolean exists = reservationRepository.existsByVehiculeIdAndDateRangeExcluding(
                reservation.getVehicule().getId(),
                request.getDateDebut(),
                request.getDateFin(),
                reservationId
        );
        if (exists) {
            throw new RuntimeException("Véhicule non disponible sur ces nouvelles dates");
        }

        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());

        long days = ChronoUnit.DAYS.between(request.getDateDebut(), request.getDateFin());
        reservation.setMontant(days * reservation.getVehicule().getPrixParJour());

        return mapToResponse(reservationRepository.save(reservation));
    }

    // valider() appelée depuis le controller Admin
    public ReservationResponse valider(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        if (reservation.getStatutReservation() != statutReservation.EN_ATTENTE) {
            throw new RuntimeException("Seule une réservation EN_ATTENTE peut être validée");
        }

        reservation.setStatutReservation(statutReservation.CONFIRMEE);
        return mapToResponse(reservationRepository.save(reservation));
    }

    // refuser() appelée depuis le controller Admin
    public ReservationResponse refuser(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        if (reservation.getStatutReservation() != statutReservation.EN_ATTENTE) {
            throw new RuntimeException("Seule une réservation EN_ATTENTE peut être refusée");
        }

        reservation.setStatutReservation(statutReservation.REFUSEE);
        return mapToResponse(reservationRepository.save(reservation));
    }

    // les http req GETS
    public List<ReservationResponse> getReservationsByClient(Long clientId) {
        return reservationRepository.findByClientId(clientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ReservationResponse> getReservationsByVehicule(Long vehiculeId) {
        return reservationRepository.findByVehiculeId(vehiculeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));
        return mapToResponse(reservation);
    }

    // MAPPER entité → DTO
    private ReservationResponse mapToResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .vehiculeId(r.getVehicule().getId())
                .dateDebut(r.getDateDebut())
                .dateFin(r.getDateFin())
                .montant(r.getMontant())
                .statut(r.getStatutReservation())
                .dateCreation(r.getDateCreation())
                .build();
    }
}