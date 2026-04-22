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

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
//^^Remplace le constructeur public ReservationService(ReservationRepository reservationRepository) {this.reservationRepository = reservationRepository;}
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final VehiculeRepository vehiculeRepository;

    public ReservationResponse createReservation(Long clientId, ReservationRequest request) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        // 🔴 Vérifier disponibilité
        boolean exists = reservationRepository
                .existsByVehiculeIdAndDateRange(
                        vehicule.getId(),
                        request.getDateFin(),
                        request.getDateDebut()
                );

        if (exists) {
            throw new RuntimeException("Véhicule déjà réservé sur cette période");
        }

        // 🧱 Mapping DTO → Entity
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());
        reservation.setDateCreation(LocalDate.now());
        reservation.setStatutReservation(statutReservation.EN_ATTENTE);

        // 💰 Calcul montant
        long days = ChronoUnit.DAYS.between(request.getDateDebut(), request.getDateFin());
        reservation.setMontant(days * 100.0); // à adapter

        Reservation saved = reservationRepository.save(reservation);

        return mapToResponse(saved);
    }

    // ✅ GET BY CLIENT
    public List<ReservationResponse> getReservationsByClient(Long clientId) {
        return reservationRepository.findByClientId(clientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ GET BY VEHICULE
    public List<ReservationResponse> getReservationsByVehicule(Long vehiculeId) {
        return reservationRepository.findByVehiculeId(vehiculeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ GET BY ID
    public ReservationResponse getReservationById(BigInteger id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        return mapToResponse(reservation);
    }

    // 🔄 ENTITY → DTO
    private ReservationResponse mapToResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId().longValue())
                .vehiculeId(r.getVehicule().getId())
                .dateDebut(r.getDateDebut())
                .dateFin(r.getDateFin())
                .montant(r.getMontant())
                .statut(r.getStatutReservation())
                .dateCreation(r.getDateCreation())
                .build();
    }
}