package com.autoloc.service;

import com.autoloc.dto.MaintenanceRequest;
import com.autoloc.dto.MaintenanceResponse;
import com.autoloc.enums.statutMaintenance;
import com.autoloc.enums.statutVehicule;
import com.autoloc.exception.MaintenanceNotFoundException;
import com.autoloc.exception.TechnicienNotFoundException;
import com.autoloc.exception.VehiculeNotFoundException;
import com.autoloc.model.OrdreMaintenance;
import com.autoloc.model.Technicien;
import com.autoloc.model.Vehicule;
import com.autoloc.repository.MaintenanceRepository;
import com.autoloc.repository.TechnicienRepository;
import com.autoloc.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MaintenanceService — méthodes du diagramme de classes :
 *
 *   Admin :
 *     +DeclencherMaintenance(Vehicule v): OrdreMaintenance
 *     +CloturerMaintenance(OrdreMaintenance o): void
 *
 *   OrdreMaintenance :
 *     +assigner(Mechanicien m): void
 *     +resoudre(): void
 */

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehiculeRepository    vehiculeRepository;
    private final TechnicienRepository  technicienRepository;

    // ─── declencherMaintenance ────────────────────────────────────────────

    /**
     * Diagramme Admin : +DeclencherMaintenance(Vehicule v): OrdreMaintenance
     *
     * L'admin signale une panne sur un véhicule.
     * Effets automatiques :
     *   - ordre créé avec statut = SIGNALE
     *   - véhicule → EN_MAINTENANCE
     */
    public MaintenanceResponse declencherMaintenance(MaintenanceRequest request) {

        /*
        Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(() -> new VehiculeNotFoundException(request.getVehiculeId()));
         **/

        Vehicule vehicule = vehiculeRepository.findByImmatriculation(request.getVehiculeImmatriculation())
                .orElseThrow(() -> new VehiculeNotFoundException(request.getVehiculeImmatriculation()));

        OrdreMaintenance ordre = new OrdreMaintenance();
        ordre.setImmatriculation(request.getVehiculeImmatriculation());
        ordre.setTypeReparation(request.getTypeReparation());
        ordre.setDescription(request.getDescription());
        ordre.setDateSignal(LocalDate.now());
        ordre.setStatut(statutMaintenance.SIGNALE);
        ordre.setVehicule(vehicule);

        // Véhicule → EN_MAINTENANCE
        vehicule.setStatut(statutVehicule.EN_MAINTENANCE);
        vehiculeRepository.save(vehicule);

        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── assigner ─────────────────────────────────────────────────────────

    /**
     * Diagramme OrdreMaintenance : +assigner(Mechanicien m): void
     *
     * Assigne un technicien disponible à un ordre SIGNALE.
     * Effets automatiques :
     *   - ordre → EN_COURS
     *   - technicien → disponible = false
     */
    public MaintenanceResponse assigner(Long ordreId, Long technicienId) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        if (ordre.getStatut() != statutMaintenance.SIGNALE) {
            throw new IllegalStateException(
                    "Impossible d'assigner : l'ordre n'est pas SIGNALE"
            );
        }

        Technicien technicien = technicienRepository.findById(technicienId)
                .orElseThrow(() -> new TechnicienNotFoundException(technicienId));

        if (!technicien.isDisponible()) {
            throw new IllegalStateException(
                    "Technicien id=" + technicienId + " non disponible"
            );
        }

        ordre.setTechnicien(technicien);
        ordre.setStatut(statutMaintenance.EN_COURS);

        // Technicien → indisponible
        technicien.setDisponible(false);
        technicienRepository.save(technicien);

        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── resoudre ─────────────────────────────────────────────────────────

    /**
     * Diagramme OrdreMaintenance : +resoudre(): void
     *
     * Marque l'ordre comme résolu.
     * Effets automatiques :
     *   - ordre → RESOLU + dateResolution = aujourd'hui
     *   - véhicule → DISPONIBLE
     *   - technicien → disponible = true
     */
    public MaintenanceResponse resoudre(Long ordreId, Double coutReel) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        if (ordre.getStatut() != statutMaintenance.EN_COURS) {
            throw new IllegalStateException(
                    "Impossible de résoudre : l'ordre n'est pas EN_COURS"
            );
        }

        // Résoudre l'ordre
        ordre.setStatut(statutMaintenance.RESOLU);
        ordre.setDateResolution(LocalDate.now());
        ordre.setCoutReparation(coutReel);

        // Véhicule → DISPONIBLE
        Vehicule vehicule = ordre.getVehicule();
        vehicule.setStatut(statutVehicule.DISPONIBLE);
        vehiculeRepository.save(vehicule);

        // Technicien → disponible
        Technicien technicien = ordre.getTechnicien();
        if (technicien != null) {
            technicien.setDisponible(true);
            technicienRepository.save(technicien);
        }

        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── cloturerMaintenance ──────────────────────────────────────────────

    /**
     * Diagramme Admin : +CloturerMaintenance(OrdreMaintenance o): void
     *
     * L'admin ferme définitivement un ordre (RESOLU ou ABANDONNE).
     * Utilisé pour les cas où l'admin décide d'abandonner la réparation.
     * Effets automatiques :
     *   - ordre → ABANDONNE
     *   - véhicule → HORS_SERVICE
     *   - technicien → disponible = true
     */
    public MaintenanceResponse cloturerMaintenance(Long ordreId) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        ordre.setStatut(statutMaintenance.ABANDONNE);
        ordre.setDateResolution(LocalDate.now());

        // Véhicule → HORS_SERVICE
        Vehicule vehicule = ordre.getVehicule();
        vehicule.setStatut(statutVehicule.HORS_SERVICE);
        vehiculeRepository.save(vehicule);

        // Technicien → disponible
        Technicien technicien = ordre.getTechnicien();
        if (technicien != null) {
            technicien.setDisponible(true);
            technicienRepository.save(technicien);
        }

        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── toResponse : entité → DTO ────────────────────────────────────────

    public MaintenanceResponse toResponse(OrdreMaintenance ordre) {
        MaintenanceResponse r = new MaintenanceResponse();
        r.setId(ordre.getId());
        r.setTypeReparation(ordre.getTypeReparation());
        r.setDescription(ordre.getDescription());
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