package com.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.autoloc.model.Paiement;
import com.autoloc.enums.statutPaiement ;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByReservationId(Long reservationId);

    List<Paiement> findByStatutPaiement(statutPaiement statut);

}