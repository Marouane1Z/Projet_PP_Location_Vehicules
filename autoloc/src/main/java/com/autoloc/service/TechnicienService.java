package com.autoloc.service;

import com.autoloc.dto.MaintenanceResponse;
import com.autoloc.dto.TechnicienRequest;
import com.autoloc.dto.TechnicienResponse;
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

@Service
@RequiredArgsConstructor
@Transactional
public class TechnicienService {

    private final TechnicienRepository  technicienRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final PasswordEncoder       passwordEncoder;

    // ─── creerTechnicien ─────────────────────────────────────────────────

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
        technicien.setRole(userRole.Technicien);
        technicien.setActif(true);

        // Champs propres à Technicien
        technicien.setSpecialite(request.getSpecialite());
        technicien.setDisponible(true);

        return toResponse(technicienRepository.save(technicien));
    }

    // ─── modifierTechnicien ───────────────────────────────────────────────

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
    public MaintenanceResponse demarrerReparation(Long ordreId) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        ordre.setStatut(statutMaintenance.EN_COURS);
        return toMaintenanceResponse(maintenanceRepository.save(ordre));
    }

    // ─── cloturerReparation ───────────────────────────────────────────────
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

    public MaintenanceResponse updateStatus(Long ordreId, statutMaintenance statut) {

        OrdreMaintenance ordre = maintenanceRepository.findById(ordreId)
                .orElseThrow(() -> new MaintenanceNotFoundException(ordreId));

        ordre.setStatut(statut);
        return toMaintenanceResponse(maintenanceRepository.save(ordre));
    }
    // ─── findAll ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<TechnicienResponse> findAll() {
        return technicienRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── findById ────────────────────────────────────────
    @Transactional(readOnly = true)
    public TechnicienResponse findById(Long id) {
        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new TechnicienNotFoundException(id));
        return toResponse(technicien);
    }

    // ─── findDisponibles ─────────────────────────────────
    @Transactional(readOnly = true)
    public List<TechnicienResponse> findDisponibles() {
        return technicienRepository.findByDisponibleTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
        r.setDisponible(technicien.isDisponible());
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