package com.autoloc.controller;

import com.autoloc.dto.MaintenanceRequest;
import com.autoloc.dto.MaintenanceResponse;
import com.autoloc.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * MaintenanceController — basé sur MaintenanceService.java
 *
 * Méthodes disponibles dans le Service :
 *   declencherMaintenance, assigner, resoudre, cloturerMaintenance
 */
@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    // ─── POST — declencherMaintenance ─────────────────────────────────────
    // L'admin signale une panne sur un véhicule via son immatriculation

    @PostMapping
    public ResponseEntity<MaintenanceResponse> declencher(
            @RequestBody @Valid MaintenanceRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(maintenanceService.declencherMaintenance(request));
    }

    // ─── PATCH — assigner ─────────────────────────────────────────────────
    // ex : PATCH /api/maintenance/5/assigner?technicienId=3

    @PatchMapping("/{id}/assigner")
    public ResponseEntity<MaintenanceResponse> assigner(
            @PathVariable Long id,
            @RequestParam Long technicienId) {
        return ResponseEntity.ok(maintenanceService.assigner(id, technicienId));
    }

    // ─── PATCH — resoudre ─────────────────────────────────────────────────
    // ex : PATCH /api/maintenance/5/resoudre?coutReel=250.0

    @PatchMapping("/{id}/resoudre")
    public ResponseEntity<MaintenanceResponse> resoudre(
            @PathVariable Long id,
            @RequestParam Double coutReel) {
        return ResponseEntity.ok(maintenanceService.resoudre(id, coutReel));
    }

    // ─── PATCH — cloturerMaintenance ──────────────────────────────────────
    // ex : PATCH /api/maintenance/5/cloturer

    @PatchMapping("/{id}/cloturer")
    public ResponseEntity<MaintenanceResponse> cloturer(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.cloturerMaintenance(id));
    }
}