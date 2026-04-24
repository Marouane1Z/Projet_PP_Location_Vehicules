package com.autoloc.repository;

import com.autoloc.enums.statutVehicule;
import com.autoloc.model.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    List<Vehicule> findByStatut(statutVehicule statut);

    List<Vehicule> findByType(String type);

    boolean existsByImmatriculation(String immatriculation);

    @Query("SELECT v FROM Voiture v")
    List<Vehicule> findAllVoitures();

    @Query("SELECT v FROM Camion v")
    List<Vehicule> findAllCamions();

    @Query("SELECT v FROM Voiture v WHERE v.statut = :statut")
    List<Vehicule> findVoituresByStatut(@Param("statut") statutVehicule statut);

    @Query("SELECT v FROM Camion v WHERE v.statut = :statut")
    List<Vehicule> findCamionsByStatut(@Param("statut") statutVehicule statut);

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    List<Vehicule> findByMarqueIgnoreCase(String marque);

    List<Vehicule> findByModeleIgnoreCase(String modele);

    List<Vehicule> findByMarqueContainingIgnoreCaseOrModeleContainingIgnoreCaseOrImmatriculationContainingIgnoreCase(
            String marque,
            String modele,
            String immatriculation
    );
}