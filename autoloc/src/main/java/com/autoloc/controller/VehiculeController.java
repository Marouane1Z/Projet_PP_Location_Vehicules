package com.autoloc.controller;

import com.autoloc.dto.VehiculeRequest;
import com.autoloc.dto.VehiculeResponse;
import com.autoloc.enums.statutVehicule;
import com.autoloc.service.VehiculeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * VehiculeController — basé sur VehiculeService.java
 *
 * Méthodes disponibles dans le Service :
 *   ajouterVehicule, modifier, supprimer
 *   findById, findAll, findAllVoitures, findAllCamions
 *   findByImmatriculation, findByMarque, findByModele, rechercher
 *   changerStatut
 */
@RestController
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final VehiculeService vehiculeService;

    // ─── POST — ajouterVehicule ───────────────────────────────────────────

    @PostMapping()
    public ResponseEntity<VehiculeResponse> ajouter(@RequestBody @Valid VehiculeRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vehiculeService.ajouterVehicule(request));
    }

    // ─── PUT — modifier ───────────────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeResponse> modifier(
            @PathVariable Long id,
            @RequestBody @Valid VehiculeRequest request) {
        return ResponseEntity.ok(vehiculeService.modifier(id, request));
    }

    // ─── DELETE — supprimer ───────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        vehiculeService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    // ─── GET — findAll ────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<VehiculeResponse>> getAll() {
        return ResponseEntity.ok(vehiculeService.findAll());
    }

    // ─── GET — findById ───────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.findById(id));
    }

    // ─── GET — findAllVoitures ────────────────────────────────────────────

    @GetMapping("/voitures")
    public ResponseEntity<List<VehiculeResponse>> getVoitures() {
        return ResponseEntity.ok(vehiculeService.findAllVoitures());
    }

    // ─── GET — findAllCamions ─────────────────────────────────────────────

    @GetMapping("/camions")
    public ResponseEntity<List<VehiculeResponse>> getCamions() {
        return ResponseEntity.ok(vehiculeService.findAllCamions());
    }

    // ─── GET — findByImmatriculation ──────────────────────────────────────
    // ex : GET /api/vehicules/immatriculation/AB-123-CD

    @GetMapping("/immatriculation/{immatriculation}")
    public ResponseEntity<VehiculeResponse> getByImmatriculation(
            @PathVariable String immatriculation) {
        return ResponseEntity.ok(vehiculeService.findByImmatriculation(immatriculation));
    }

    // ─── GET — findByMarque ───────────────────────────────────────────────
    // ex : GET /api/vehicules/marque/Peugeot

    @GetMapping("/marque/{marque}")
    public ResponseEntity<List<VehiculeResponse>> getByMarque(
            @PathVariable String marque) {
        return ResponseEntity.ok(vehiculeService.findByMarque(marque));
    }

    // ─── GET — findByModele ───────────────────────────────────────────────
    // ex : GET /api/vehicules/modele/308

    @GetMapping("/modele/{modele}")
    public ResponseEntity<List<VehiculeResponse>> getByModele(
            @PathVariable String modele) {
        return ResponseEntity.ok(vehiculeService.findByModele(modele));
    }

    // ─── GET — rechercher ────────────────────────────────────────────────
    // ex : GET /api/vehicules/recherche?keyword=peugeot

    @GetMapping("/recherche")
    public ResponseEntity<List<VehiculeResponse>> rechercher(
            @RequestParam String keyword) {
        return ResponseEntity.ok(vehiculeService.rechercher(keyword));
    }

    // ─── PATCH — changerStatut ────────────────────────────────────────────
    // ex : PATCH /api/vehicules/5/statut?statut=EN_MAINTENANCE

    @PatchMapping("/{id}/statut")
    public ResponseEntity<VehiculeResponse> changerStatut(
            @PathVariable Long id,
            @RequestParam statutVehicule statut) {
        return ResponseEntity.ok(vehiculeService.changerStatut(id, statut));
    }
}