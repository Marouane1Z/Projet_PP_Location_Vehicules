package com.autoloc.repository;

import com.autoloc.enums.statutReservation;
import com.autoloc.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> { // ✅ Long

    List<Reservation> findByClientId(Long clientId);

    List<Reservation> findByVehiculeId(Long vehiculeId);

    List<Reservation> findByStatutReservation(statutReservation statut);

    List<Reservation> findByClientIdAndStatutReservation(Long clientId, statutReservation statut);

    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.vehicule.id = :vehiculeId
        AND r.statutReservation NOT IN ('ANNULEE', 'REFUSEE')
        AND r.dateDebut < :dateFin
        AND r.dateFin > :dateDebut
    """)
    boolean existsByVehiculeIdAndDateRange(
            @Param("vehiculeId") Long vehiculeId,
            @Param("dateDebut")  LocalDate dateDebut,
            @Param("dateFin")    LocalDate dateFin
    );

    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.vehicule.id = :vehiculeId
        AND r.id != :excludeId
        AND r.statutReservation NOT IN ('ANNULEE', 'REFUSEE')
        AND r.dateDebut < :dateFin
        AND r.dateFin > :dateDebut
    """)
    boolean existsByVehiculeIdAndDateRangeExcluding(
            @Param("vehiculeId") Long vehiculeId,
            @Param("dateDebut")  LocalDate dateDebut,
            @Param("dateFin")    LocalDate dateFin,
            @Param("excludeId")  Long excludeId
    );
}