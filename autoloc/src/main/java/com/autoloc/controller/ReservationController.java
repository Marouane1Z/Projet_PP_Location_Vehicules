package com.autoloc.controller;

import com.autoloc.dto.ReservationRequest;
import com.autoloc.dto.ReservationResponse;
import com.autoloc.model.User;
import com.autoloc.service.ClientService;
import com.autoloc.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ClientService      clientService;

    // POST /api/reservations
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReservationResponse> creerReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clientService.reserverVehicule(user.getId(), request));
    }

    // GET /api/reservations/mes-reservations
    // Cette route DOIT être déclarée AVANT /{id}
    @GetMapping("/mes-reservations")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ReservationResponse>> mesReservations(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                clientService.afficherReservations(user.getId())
        );
    }

     // GET /api/reservations/{id}
     // CLIENT : uniquement la sienne / ADMIN : toutes
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ReservationResponse> getById(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        ReservationResponse reservation = reservationService.getReservationById(id);

        // Un CLIENT ne peut voir que ses propres réservations
        if (user.getRole().name().equals("CLIENT") &&
                !reservation.getClientId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(reservation);
    }

    // PUT /api/reservations/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReservationResponse> modifierReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                clientService.modifierReservations(user.getId(), id, request)
        );
    }


     // DELETE /api/reservations/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> annulerReservation(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        clientService.annulerReservations(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    //  ENDPOINTS ADMIN
     // Retourne toutes les réservations.
     // GET /api/reservations
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ReservationResponse>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }


     // Retourne toutes les réservations d'un véhicule.
     // GET /api/reservations/vehicule/{vehiculeId}

    @GetMapping("/vehicule/{vehiculeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ReservationResponse>> getByVehicule(
            @PathVariable Long vehiculeId) {
        return ResponseEntity.ok(
                reservationService.getReservationsByVehicule(vehiculeId)
        );
    }

     // L'admin valide une réservation EN_ATTENTE.
     // PATCH /api/reservations/{id}/valider

    @PatchMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ReservationResponse> valider(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.valider(id));
    }

     // L'admin refuse une réservation EN_ATTENTE.
     // PATCH /api/reservations/{id}/refuser
    @PatchMapping("/{id}/refuser")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ReservationResponse> refuser(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.refuser(id));
    }
}