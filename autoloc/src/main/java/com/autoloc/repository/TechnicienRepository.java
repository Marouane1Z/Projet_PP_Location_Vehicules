package com.autoloc.repository;

import com.autoloc.model.Technicien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Technicien.
 * Gère l'accès BDD à la table "technicien"
 * (jointure avec la table "utilisateur" — stratégie JOINED).
 *
 * Champs disponibles pour les requêtes :
 *   Depuis Technicien.java :
 *     - specialite  (String)
 *     - disponible  (Boolean) — à ajouter dans Technicien.java
 *
 *   Hérités de User.java :
 *     - email       (String)
 *     - firstname   (String)
 *     - lastname    (String)
 *     - actif       (Boolean)
 *     - role        (userRole)
 */
@Repository
public interface TechnicienRepository extends JpaRepository<Technicien, Long> {

    Optional<Technicien> findByEmail(String email);

    List<Technicien> findByDisponibleTrue();

    List<Technicien> findBySpecialite(String specialite);

    List<Technicien> findByFirstname(String firstname);

    List<Technicien> findByLastname(String lastname);


}