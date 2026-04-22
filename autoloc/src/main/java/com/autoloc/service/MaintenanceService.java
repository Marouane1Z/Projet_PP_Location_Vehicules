package com.autoloc.service;

import com.autoloc.dto.maintenance.MaintenanceRequest;
import com.autoloc.dto.maintenance.MaintenanceResponse;
import com.autoloc.enums.StatutMaintenance;
import com.autoloc.enums.StatutVehicule;
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
 * Service Maintenance — logique métier des ordres de maintenance.
 *
 * Correspond aux méthodes du diagramme de classes :
 *   OrdreMaintenance : +assigner(Technicien t), +resoudre()
 *   Technicien       : +receptionOrdre(id), +demarrerReparation(id),
 *                      +cloturerReparation(id), +UpdateStatus(StatutMaintenance s)
 *
 * Statuts possibles (StatutMaintenance.java) :
 *   SIGNALE → EN_COURS → RESOLU
 *                      → ABANDONNE
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehiculeRepository    vehiculeRepository;
    private final TechnicienRepository  technicienRepository;

    // ─── CRÉER UN ORDRE (Admin) ───────────────────────────────────────────

    /**
     * L'Admin signale une panne et crée un ordre de maintenance.
     * Champs utilisés depuis OrdreMaintenance.java :
     *   typeReparation, description, dateSignal, statut, vehicule, technicien
     *
     * Effets automatiques :
     *   - véhicule → EN_MAINTENANCE
     *   - si technicienId fourni → statut = EN_COURS + technicien.disponible = false
     *   - si pas de technicien   → statut = SIGNALE
     */
    public MaintenanceResponse creerOrdre(MaintenanceRequest request) {

        // Récupérer le véhicule (depuis VehiculeRepository)
        Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(() -> new VehiculeNotFoundException(request.getVehiculeId()));

        // Préparer l'ordre
        OrdreMaintenance ordre = new OrdreMaintenance();
        ordre.setTypeReparation(request.getTypeReparation());
        ordre.setDescription(request.getDescription());
        ordre.setDateSignal(LocalDate.now());
        ordre.setStatut(StatutMaintenance.SIGNALE);
        ordre.setVehicule(vehicule);

        // Assigner technicien si fourni dans le DTO
        if (request.getTechnicienId() != null) {
            Technicien technicien = technicienRepository
                    .findById(request.getTechnicienId())
                    .orElseThrow(() -> new TechnicienNotFoundException(
                            request.getTechnicienId()
                    ));

            // Règle métier : technicien doit être disponible
            if (!technicien.getDisponible()) {
                throw new IllegalStateException(
                        "Le technicien id=" + request.getTechnicienId()
                                + " n'est pas disponible"
                );
            }

            ordre.setTechnicien(technicien);
            ordre.setStatut(StatutMaintenance.EN_COURS);

            // Technicien devient indisponible
            technicien.setDisponible(false);
            technicienRepository.save(technicien);
        }

        // Véhicule passe EN_MAINTENANCE
        vehicule.changerStatut(StatutVehicule.EN_MAINTENANCE);
        vehiculeRepository.save(vehicule);

        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── ASSIGNER UN TECHNICIEN ───────────────────────────────────────────

    /**
     * Assigne un technicien à un ordre SIGNALE.
     * Correspond à : +assigner(Technicien t) dans OrdreMaintenance du diagramme.
     */
    public MaintenanceResponse assigner(Long ordreId, Long technicienId) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        // Règle métier : peut assigner uniquement si SIGNALE
        if (ordre.getStatut() != StatutMaintenance.SIGNALE) {
            throw new IllegalStateException(
                    "Impossible d'assigner un technicien : "
                            + "l'ordre n'est pas en statut SIGNALE"
            );
        }

        Technicien technicien = technicienRepository.findById(technicienId)
                .orElseThrow(() -> new TechnicienNotFoundException(technicienId));

        if (!technicien.getDisponible()) {
            throw new IllegalStateException(
                    "Le technicien id=" + technicienId + " n'est pas disponible"
            );
        }

        ordre.setTechnicien(technicien);
        ordre.setStatut(StatutMaintenance.EN_COURS);

        technicien.setDisponible(false);
        technicienRepository.save(technicien);

        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── DÉMARRER RÉPARATION (Technicien) ────────────────────────────────

    /**
     * Correspond à : +demarrerReparation(id) dans Technicien du diagramme.
     * Passe le statut à EN_COURS.
     */
    public MaintenanceResponse demarrerReparation(Long ordreId) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        if (ordre.getStatut() != StatutMaintenance.SIGNALE
                && ordre.getStatut() != StatutMaintenance.EN_COURS) {
            throw new IllegalStateException(
                    "Impossible de démarrer : statut actuel = " + ordre.getStatut()
            );
        }

        ordre.setStatut(StatutMaintenance.EN_COURS);
        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── CLÔTURER RÉPARATION (Technicien) ────────────────────────────────

    /**
     * Correspond à : +cloturerReparation(id) et +resoudre() dans le diagramme.
     *
     * Effets automatiques :
     *   - statut ordre → RESOLU
     *   - dateResolution → aujourd'hui
     *   - coutReparation → enregistré
     *   - véhicule → DISPONIBLE
     *   - technicien.disponible → true
     */
    public MaintenanceResponse cloturerReparation(Long ordreId, Double coutReel) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        // Règle métier : peut clôturer uniquement si EN_COURS
        if (ordre.getStatut() != StatutMaintenance.EN_COURS) {
            throw new IllegalStateException(
                    "Impossible de clôturer : l'ordre doit être EN_COURS"
            );
        }

        // Mettre à jour l'ordre (champs de OrdreMaintenance.java)
        ordre.setStatut(StatutMaintenance.RESOLU);
        ordre.setDateResolution(LocalDate.now());
        ordre.setCoutReparation(coutReel);

        // Véhicule redevient DISPONIBLE
        Vehicule vehicule = ordre.getVehicule();
        vehicule.changerStatut(StatutVehicule.DISPONIBLE);
        vehiculeRepository.save(vehicule);

        // Technicien redevient disponible
        Technicien technicien = ordre.getTechnicien();
        if (technicien != null) {
            technicien.setDisponible(true);
            technicienRepository.save(technicien);
        }

        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── METTRE À JOUR LE STATUT (Technicien) ────────────────────────────

    /**
     * Correspond à : +UpdateStatus(StatutMaintenance s) dans Technicien du diagramme.
     * Permet de changer manuellement le statut d'un ordre.
     */
    public MaintenanceResponse updateStatut(Long ordreId, StatutMaintenance statut) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        ordre.setStatut(statut);
        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── ABANDONNER ───────────────────────────────────────────────────────

    /**
     * Statut ABANDONNE (depuis StatutMaintenance.java).
     * Effets :
     *   - véhicule → HORS_SERVICE
     *   - technicien → disponible si assigné
     */
    public MaintenanceResponse abandonner(Long ordreId) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        ordre.setStatut(StatutMaintenance.ABANDONNE);

        // Véhicule passe HORS_SERVICE
        Vehicule vehicule = ordre.getVehicule();
        vehicule.changerStatut(StatutVehicule.HORS_SERVICE);
        vehiculeRepository.save(vehicule);

        // Technicien redevient disponible
        Technicien technicien = ordre.getTechnicien();
        if (technicien != null) {
            technicien.setDisponible(true);
            technicienRepository.save(technicien);
        }

        return toResponse(maintenanceRepository.save(ordre));
    }

    // ─── LIRE ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public MaintenanceResponse findById(Long id) {
        return toResponse(
                maintenanceRepository.findById(id)
                        .orElseThrow(() -> new MaintenanceNotFoundException(id))
        );
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> findAll() {
        return maintenanceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> findByStatut(StatutMaintenance statut) {
        return maintenanceRepository.findByStatut(statut)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> findByTechnicienId(Long technicienId) {
        return maintenanceRepository.findByTechnicienId(technicienId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> findByVehiculeId(Long vehiculeId) {
        return maintenanceRepository.findByVehiculeId(vehiculeId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── toResponse : entité → DTO ────────────────────────────────────────

    /**
     * Convertit OrdreMaintenance en MaintenanceResponse.
     * Champs de l'image : id, typeReparation, statut,
     *                     dateSignal, dateResolution, coutReparation
     */
    public MaintenanceResponse toResponse(OrdreMaintenance ordre) {
        MaintenanceResponse r = new MaintenanceResponse();

        // Champs directs de OrdreMaintenance.java
        r.setId(ordre.getId());
        r.setTypeReparation(ordre.getTypeReparation());
        r.setDescription(ordre.getDescription());
        r.setStatut(ordre.getStatut());
        r.setDateSignal(ordre.getDateSignal());
        r.setDateResolution(ordre.getDateResolution());
        r.setCoutReparation(ordre.getCoutReparation());

        // Infos véhicule (@ManyToOne dans OrdreMaintenance.java)
        if (ordre.getVehicule() != null) {
            r.setVehiculeId(ordre.getVehicule().getId());
            r.setVehiculeMarque(ordre.getVehicule().getMarque());
            r.setVehiculeModele(ordre.getVehicule().getModele());
            r.setVehiculeImmatriculation(ordre.getVehicule().getImmatriculation());
        }

        // Infos technicien (@ManyToOne dans OrdreMaintenance.java)
        // firstname + lastname hérités de User.java
        if (ordre.getTechnicien() != null) {
            r.setTechnicienId(ordre.getTechnicien().getId());
            r.setTechnicienPrenom(ordre.getTechnicien().getFirstname());
            r.setTechnicienNom(ordre.getTechnicien().getLastname());
        }

        return r;
    }
}