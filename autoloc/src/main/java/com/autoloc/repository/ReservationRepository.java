package com.autoloc.repository;

import com.autoloc.enums.statutReservation;
import com.autoloc.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, BigInteger> {

    List<Reservation> findByClientId(Long clientId);

    List<Reservation> findByVehiculeId(Long vehiculeId);

    List<Reservation> findByStatutReservation(statutReservation statut);

    // Par client + statut (très utile)
    List<Reservation> findByClientIdAndStatutReservation(Long clientId, statutReservation statut);

    boolean existsByVehiculeIdAndDateRange(Long vehiculeId, LocalDate dateDebut, LocalDate dateFin);
}