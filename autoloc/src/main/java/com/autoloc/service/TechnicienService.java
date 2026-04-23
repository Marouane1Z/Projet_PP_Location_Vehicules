package com.autoloc.service;

import com.autoloc.dto.technicien.TechnicienRequest;
import com.autoloc.dto.technicien.TechnicienResponse;
import com.autoloc.enums.statutMaintenance;
import com.autoloc.enums.userRole;
import com.autoloc.exception.MaintenanceNotFoundException;
import com.autoloc.exception.TechnicienNotFoundException;
import com.autoloc.model.OrdreMaintenance;
import com.autoloc.model.Technicien;
import com.autoloc.repository.MaintenanceRepository;
import com.autoloc.repository.TechnicienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TechnicienService — méthodes du diagramme de classes :
 *
 *   Admin :
 *     +creerTechnicien(Technicien a): Technicien
 *     +modifierTechnicien(BIGINT id): void
 *     +supprimerTechnicien(BIGINT id): void
 *
 *   Technicien :
 *     +receptionOrdre(id): void
 *     +demarrerReparation(id): void
 *     +cloturerReparation(id): void
 *     +UpdateStatus(StatutMaintenance s): void
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TechnicienService {

    private final TechnicienRepository  technicienRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final PasswordEncoder       passwordEncoder;

    // ─── creerTechnicien ─────────────────────────────────────────────────

    /**
     * Diagramme Admin : +creerTechnicien(Technicien a): Technicien
     *
     * Crée un compte technicien.
     * Appelé uniquement par l'Admin — le technicien ne peut pas s'inscrire seul.
     * Règles métier :
     *   - email unique
     *   - mot de passe hashé avec BCrypt
     *   - disponible = true par défaut
     */
    public TechnicienResponse creerTechnicien(TechnicienRequest request) {

        if (technicienRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Email déjà utilisé : " + request.getEmail()
            );
        }

        Technicien technicien = new Technicien();

        // Champs hérités de User
        technicien.setFirstname(request.getFirstname());
        technicien.setLastname(request.getLastname());
        technicien.setEmail(request.getEmail());
        technicien.setPassword(passwordEncoder.encode(request.getPassword()));
        technicien.setRole(userRole.Mechanicien);
        technicien.setActif(true);

        // Champs propres à Technicien
        technicien.setSpecialite(request.getSpecialite());
        technicien.setDisponible(true);

        return toResponse(technicienRepository.save(technicien));
    }

    // ─── modifierTechnicien ───────────────────────────────────────────────

    /**
     * Diagramme Admin : +modifierTechnicien(BIGINT id): void
     *
     * Modifie les informations d'un technicien existant.
     */
    public TechnicienResponse modifierTechnicien(Long id, TechnicienRequest request) {

        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new TechnicienNotFoundException(id));

        // Champs hérités de User
        technicien.setFirstname(request.getFirstname());
        technicien.setLastname(request.getLastname());

        // Champs propres à Technicien
        technicien.setSpecialite(request.getSpecialite());

        return toResponse(technicienRepository.save(technicien));
    }

    // ─── supprimerTechnicien ──────────────────────────────────────────────

    /**
     * Diagramme Admin : +supprimerTechnicien(BIGINT id): void
     *
     * Règle métier :
     *   - impossible si le technicien a des ordres EN_COURS
     */
    public void supprimerTechnicien(Long id) {

        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new TechnicienNotFoundException(id));

        boolean aDesOrdresEnCours = technicien.getOrdreMaintenances()
                .stream()
                .anyMatch(o -> o.getStatut() == statutMaintenance.EN_COURS);

        if (aDesOrdresEnCours) {
            throw new IllegalStateException(
                    "Impossible de supprimer : technicien avec des réparations EN_COURS"
            );
        }

        technicienRepository.delete(technicien);
    }

    // ─── receptionOrdre ───────────────────────────────────────────────────

    /**
     * Diagramme Technicien : +receptionOrdre(id): void
     *
     * Le technicien prend connaissance de l'ordre assigné.
     * Statut passe de SIGNALE → EN_COURS.
     */
    public MaintenanceResponse receptionOrdre(Long ordreId) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        if (ordre.getStatut() == statutMaintenance.SIGNALE) {
            ordre.setStatut(statutMaintenance.EN_COURS);
            maintenanceRepository.save(ordre);
        }

        return toMaintenanceResponse(ordre);
    }

    // ─── demarrerReparation ───────────────────────────────────────────────

    /**
     * Diagramme Technicien : +demarrerReparation(id): void
     *
     * Le technicien démarre officiellement la réparation.
     * Statut = EN_COURS.
     */
    public MaintenanceResponse demarrerReparation(Long ordreId) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        ordre.setStatut(statutMaintenance.EN_COURS);
        return toMaintenanceResponse(maintenanceRepository.save(ordre));
    }

    // ─── cloturerReparation ───────────────────────────────────────────────

    /**
     * Diagramme Technicien : +cloturerReparation(id): void
     *
     * Le technicien termine la réparation.
     * Délègue à MaintenanceService.resoudre() pour la logique complète
     * (véhicule → DISPONIBLE, technicien → disponible = true).
     */
    public MaintenanceResponse cloturerReparation(Long ordreId, Double coutReel) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        if (ordre.getStatut() != statutMaintenance.EN_COURS) {
            throw new IllegalStateException(
                    "Impossible de clôturer : l'ordre n'est pas EN_COURS"
            );
        }

        ordre.setStatut(statutMaintenance.RESOLU);
        ordre.setCoutReparation(coutReel);

        // Technicien → disponible
        if (ordre.getTechnicien() != null) {
            ordre.getTechnicien().setDisponible(true);
            technicienRepository.save(ordre.getTechnicien());
        }

        return toMaintenanceResponse(maintenanceRepository.save(ordre));
    }

    // ─── updateStatus ─────────────────────────────────────────────────────

    /**
     * Diagramme Technicien : +UpdateStatus(StatutMaintenance s): void
     *
     * Change manuellement le statut d'un ordre.
     * Utilisé pour des corrections ou cas particuliers.
     */
    public MaintenanceResponse updateStatus(Long ordreId, statutMaintenance statut) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        ordre.setStatut(statut);
        return toMaintenanceResponse(maintenanceRepository.save(ordre));
    }

    // ─── toResponse ──────────────────────────────────────────────────────

    public TechnicienResponse toResponse(Technicien technicien) {
        TechnicienResponse r = new TechnicienResponse();
        r.setId(technicien.getId());
        r.setFirstname(technicien.getFirstname());
        r.setLastname(technicien.getLastname());
        r.setEmail(technicien.getEmail());
        r.setPhone(technicien.getPhone());
        r.setActif(technicien.getActif());
        r.setSpecialite(technicien.getSpecialite());
        r.setDisponible(technicien.getDisponible());
        return r;
    }

    private MaintenanceResponse toMaintenanceResponse(OrdreMaintenance ordre) {
        MaintenanceResponse r = new MaintenanceResponse();
        r.setId(ordre.getId());
        r.setTypeReparation(ordre.getTypeReparation());
        r.setStatut(ordre.getStatut());
        r.setDateSignal(ordre.getDateSignal());
        r.setDateResolution(ordre.getDateResolution());
        r.setCoutReparation(ordre.getCoutReparation());

        if (ordre.getVehicule() != null) {
            r.setVehiculeId(ordre.getVehicule().getId());
            r.setVehiculeMarque(ordre.getVehicule().getMarque());
            r.setVehiculeModele(ordre.getVehicule().getModele());
            r.setVehiculeImmatriculation(ordre.getVehicule().getImmatriculation());
        }
        if (ordre.getTechnicien() != null) {
            r.setTechnicienId(ordre.getTechnicien().getId());
            r.setTechnicienPrenom(ordre.getTechnicien().getFirstname());
            r.setTechnicienNom(ordre.getTechnicien().getLastname());
        }
        return r;
    }
}