package com.autoloc.repository;

import com.autoloc.enums.StatutVehicule;
import com.autoloc.model.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    // Trouver listes des vehicules en cherchant par status
    List<Vehicule> findByStatut(StatutVehicule statut);

    // Trouver listes des vehicules en cherchant par type
    List<Vehicule> findByType(String type);

    // verifier si un vehicule existe en entrant sa matriculation
    boolean existsByImmatriculation(String immatriculation);

    //@Query("SELECT v FROM Vehicule v WHERE TYPE(v) = Voiture")
    List<Vehicule> findAllVoitures();

    //@Query("SELECT v FROM Vehicule v WHERE TYPE(v) = Camion")
    List<Vehicule> findAllCamions();

    // trouver par statut et voitures
    List<Vehicule> findVoituresByStatut(StatutVehicule statut);

    // trouver par statut et camions
    List<Vehicule> findCamionsByStatut(StatutVehicule statut);

}