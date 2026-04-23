package com.autoloc.controller;

import com.autoloc.dto.MaintenanceResponse;
import com.autoloc.dto.TechnicienRequest;
import com.autoloc.dto.TechnicienResponse;
import com.autoloc.enums.statutMaintenance;
import com.autoloc.service.TechnicienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TechnicienController — basé sur TechnicienService.java
 *
 * Méthodes Admin disponibles dans le Service :
 *   creerTechnicien, modifierTechnicien, supprimerTechnicien
 *
 * Méthodes Technicien disponibles dans le Service :
 *   receptionOrdre, demarrerReparation, cloturerReparation, updateStatus
 */
@RestController
@RequestMapping("/api/techniciens")
@RequiredArgsConstructor
public class TechnicienController {

    private final TechnicienService technicienService;

    // ─── POST — creerTechnicien ───────────────────────────────────────────

    @PostMapping
    public ResponseEntity<TechnicienResponse> creer(
            @RequestBody @Valid TechnicienRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(technicienService.creerTechnicien(request));
    }

    // ─── PUT — modifierTechnicien ─────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<TechnicienResponse> modifier(
            @PathVariable Long id,
            @RequestBody @Valid TechnicienRequest request) {
        return ResponseEntity.ok(technicienService.modifierTechnicien(id, request));
    }

    // ─── DELETE — supprimerTechnicien ─────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        technicienService.supprimerTechnicien(id);
        return ResponseEntity.noContent().build();
    }

    // ─── GET — findAll ────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<TechnicienResponse>> getAll() {
        return ResponseEntity.ok(technicienService.findAll());
    }

    // ─── GET — findById ───────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<TechnicienResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(technicienService.findById(id));
    }

    // ─── GET — findDisponibles ────────────────────────────────────────────

    @GetMapping("/disponibles")
    public ResponseEntity<List<TechnicienResponse>> getDisponibles() {
        return ResponseEntity.ok(technicienService.findDisponibles());
    }

    // ─── PATCH — receptionOrdre ───────────────────────────────────────────
    // ex : PATCH /api/techniciens/3/ordres/7/reception

    @PatchMapping("/{id}/ordres/{ordreId}/reception")
    public ResponseEntity<MaintenanceResponse> receptionOrdre(
            @PathVariable Long id,
            @PathVariable Long ordreId) {
        return ResponseEntity.ok(technicienService.receptionOrdre(ordreId));
    }

    // ─── PATCH — demarrerReparation ───────────────────────────────────────
    // ex : PATCH /api/techniciens/3/ordres/7/demarrer

    @PatchMapping("/{id}/ordres/{ordreId}/demarrer")
    public ResponseEntity<MaintenanceResponse> demarrerReparation(
            @PathVariable Long id,
            @PathVariable Long ordreId) {
        return ResponseEntity.ok(technicienService.demarrerReparation(ordreId));
    }

    // ─── PATCH — cloturerReparation ───────────────────────────────────────
    // ex : PATCH /api/techniciens/3/ordres/7/cloturer?coutReel=350.0

    @PatchMapping("/{id}/ordres/{ordreId}/cloturer")
    public ResponseEntity<MaintenanceResponse> cloturerReparation(
            @PathVariable Long id,
            @PathVariable Long ordreId,
            @RequestParam Double coutReel) {
        return ResponseEntity.ok(technicienService.cloturerReparation(ordreId, coutReel));
    }

    // ─── PATCH — updateStatus ─────────────────────────────────────────────
    // ex : PATCH /api/techniciens/3/ordres/7/statut?statut=EN_COURS

    @PatchMapping("/{id}/ordres/{ordreId}/statut")
    public ResponseEntity<MaintenanceResponse> updateStatus(
            @PathVariable Long id,
            @PathVariable Long ordreId,
            @RequestParam statutMaintenance statut) {
        return ResponseEntity.ok(technicienService.updateStatus(ordreId, statut));
    }
}