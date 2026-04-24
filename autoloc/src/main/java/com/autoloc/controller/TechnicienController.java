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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/techniciens")
@RequiredArgsConstructor
public class TechnicienController {

    private final TechnicienService technicienService;

    // ─── POST — creerTechnicien (Admin) ──────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TechnicienResponse> creer(
            @RequestBody @Valid TechnicienRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(technicienService.creerTechnicien(request));
    }

    // ─── PUT — modifierTechnicien (Admin) ────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TechnicienResponse> modifier(
            @PathVariable Long id,
            @RequestBody @Valid TechnicienRequest request) {
        return ResponseEntity.ok(technicienService.modifierTechnicien(id, request));
    }

    // ─── DELETE — supprimerTechnicien (Admin) ────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        technicienService.supprimerTechnicien(id);
        return ResponseEntity.noContent().build();
    }

    // ─── GET — findAll (Admin) ────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TechnicienResponse>> getAll() {
        return ResponseEntity.ok(technicienService.findAll());
    }

    // ─── GET — findById (Admin, Technicien) ──────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIEN')")
    public ResponseEntity<TechnicienResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(technicienService.findById(id));
    }

    // ─── GET — findDisponibles (Admin) ───────────────────
    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TechnicienResponse>> getDisponibles() {
        return ResponseEntity.ok(technicienService.findDisponibles());
    }

    // ─── PATCH — receptionOrdre (Technicien) ─────────────
    @PatchMapping("/{id}/ordres/{ordreId}/reception")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ResponseEntity<MaintenanceResponse> receptionOrdre(
            @PathVariable Long id,
            @PathVariable Long ordreId) {
        return ResponseEntity.ok(technicienService.receptionOrdre(ordreId));
    }

    // ─── PATCH — demarrerReparation (Technicien) ─────────
    @PatchMapping("/{id}/ordres/{ordreId}/demarrer")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ResponseEntity<MaintenanceResponse> demarrerReparation(
            @PathVariable Long id,
            @PathVariable Long ordreId) {
        return ResponseEntity.ok(technicienService.demarrerReparation(ordreId));
    }

    // ─── PATCH — cloturerReparation (Technicien) ─────────
    @PatchMapping("/{id}/ordres/{ordreId}/cloturer")
    @PreAuthorize("hasRole('TECHNICIEN')")
    public ResponseEntity<MaintenanceResponse> cloturerReparation(
            @PathVariable Long id,
            @PathVariable Long ordreId,
            @RequestParam Double coutReel) {
        return ResponseEntity.ok(
                technicienService.cloturerReparation(ordreId, coutReel)
        );
    }

    // ─── PATCH — updateStatus (Admin, Technicien) ────────
    @PatchMapping("/{id}/ordres/{ordreId}/statut")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIEN')")
    public ResponseEntity<MaintenanceResponse> updateStatus(
            @PathVariable Long id,
            @PathVariable Long ordreId,
            @RequestParam statutMaintenance statut) {
        return ResponseEntity.ok(
                technicienService.updateStatus(ordreId, statut)
        );
    }
}