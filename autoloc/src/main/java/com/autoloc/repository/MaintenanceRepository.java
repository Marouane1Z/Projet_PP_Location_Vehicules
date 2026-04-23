package com.autoloc.repository;

import com.autoloc.enums.StatutMaintenance;
import com.autoloc.model.OrdreMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité OrdreMaintenance.
 * Gère l'accès BDD à la table "ordre_maintenance".
 *
 * Champs disponibles pour les requêtes (depuis OrdreMaintenance.java) :
 *   - statut         (StatutMaintenance)
 *   - vehicule       (@ManyToOne → vehicule_id)
 *   - technicien     (@ManyToOne → technicien_id)
 *   - admin          (@ManyToOne → admin_id)
 *   - dateSignal     (LocalDate)
 *   - dateResolution (LocalDate)
 */

@Repository
public interface MaintenanceRepository extends JpaRepository<OrdreMaintenance, Long> {

    // ─── findByVehiculeId ─────────────────────────────────────────────────
    // Tous les ordres de maintenance d'un véhicule précis
    // Spring traduit : SELECT * FROM ordre_maintenance WHERE vehicule_id = ?

    List<OrdreMaintenance> findByVehiculeId(Long vehiculeId);

    // ─── findByStatut ─────────────────────────────────────────────────────
    // Tous les ordres avec un statut donné (SIGNALE, EN_COURS, RESOLU, ABANDONNE)
    // Spring traduit : SELECT * FROM ordre_maintenance WHERE statut = ?

    List<OrdreMaintenance> findByStatut(StatutMaintenance statut);

    // ─── findByTechnicienId ───────────────────────────────────────────────
    // Tous les ordres assignés à un technicien précis
    // Spring traduit : SELECT * FROM ordre_maintenance WHERE technicien_id = ?

    List<OrdreMaintenance> findByTechnicienId(Long technicienId);

    // ─── Combinaisons utiles ──────────────────────────────────────────────

    // Ordres d'un technicien filtrés par statut
    // ex : tous les ordres EN_COURS du technicien id=3
    List<OrdreMaintenance> findByTechnicienIdAndStatut(
            Long technicienId, StatutMaintenance statut
    );

    // Ordres d'un véhicule filtrés par statut
    // ex : tous les ordres SIGNALE du véhicule id=5
    List<OrdreMaintenance> findByVehiculeIdAndStatut(
            Long vehiculeId, StatutMaintenance statut
    );

    // Vérifier si un véhicule a un ordre EN_COURS
    // Utile avant de supprimer un véhicule
    boolean existsByVehiculeIdAndStatut(
            Long vehiculeId, StatutMaintenance statut
    );
}
