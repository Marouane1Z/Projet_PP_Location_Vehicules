package com.autoloc.controller;

import com.autoloc.dto.PaiementRequest;
import com.autoloc.dto.PaiementResponse;
import com.autoloc.service.PaiementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    // ─── EFFECTUER PAIEMENT (Client) ─────────────────────
    @PostMapping("/{reservationId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PaiementResponse> effectuerPaiement(
            @PathVariable Long reservationId,
            @Valid @RequestBody PaiementRequest paiementRequest) {
        return ResponseEntity.ok(
                paiementService.effectuerPaiement(reservationId, paiementRequest)
        );
    }

    // ─── RÉCUPÉRER UN PAIEMENT ───────────────────────────
    @GetMapping("/{paiementId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<PaiementResponse> getPaiement(
            @PathVariable Long paiementId) {
        return ResponseEntity.ok(paiementService.getPaiement(paiementId));
    }

    // ─── RÉCUPÉRER PAIEMENT PAR RÉSERVATION ──────────────
    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<PaiementResponse> getPaiementByReservation(
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(
                paiementService.getPaiementByReservation(reservationId)
        );
    }

    // ─── CONFIRMER ESPÈCES (Admin) ───────────────────────
    @PatchMapping("/{paiementId}/confirmer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementResponse> confirmerEspeces(
            @PathVariable Long paiementId) {
        return ResponseEntity.ok(paiementService.confirmerEspeces(paiementId));
    }

    // ─── REMBOURSER (Admin) ──────────────────────────────
    @PatchMapping("/{paiementId}/rembourser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaiementResponse> rembourser(
            @PathVariable Long paiementId) {
        return ResponseEntity.ok(paiementService.rembourser(paiementId));
    }
}