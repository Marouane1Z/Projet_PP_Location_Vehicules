package com.autoloc.controller;

import com.autoloc.dto.ClientRequest;
import com.autoloc.dto.ClientResponse;
import com.autoloc.dto.ReservationRequest;
import com.autoloc.dto.ReservationResponse;
import com.autoloc.dto.PaiementRequest;
import com.autoloc.model.User;
import com.autoloc.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    //GET /api/clients
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ClientResponse>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    //GET /api/clients/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    // Crée un client manuellement (par un Admin).
    // inscription publique passe par POST /api/auth/register
    // POST /api/clients par admin
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clientService.createClient(request));
    }

    //PUT /api/clients/{id} par admin
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ClientResponse> modifierClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.modifierInformations(id, request));
    }

    // Désactive le compte d'un client sans le supprimer.
    // PATCH /api/clients/{id}/desactiver
    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> desactiverClient(@PathVariable Long id) {
        clientService.desactiverClient(id);
        return ResponseEntity.ok().build();
    }

    //DELETE /api/clients/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> supprimerClient(@PathVariable Long id) {
        clientService.supprimerClient(id);
        return ResponseEntity.noContent().build();
    }


    // Le client connecté modifie son propre profil.
    // PUT /api/clients/me
    // Authentication est injecté automatiquement par Spring
    // depuis le SecurityContext rempli par JwtFilter.
    @PutMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponse> modifierMonProfil(
            @Valid @RequestBody ClientRequest request,
            Authentication authentication) {

        // On récupère l'id du client connecté depuis le token JWT
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                clientService.modifierInformations(user.getId(), request)
        );
    }


    // GET /api/clients/me/reservations
    @GetMapping("/me/reservations")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ReservationResponse>> mesReservations(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                clientService.afficherReservations(user.getId())
        );
    }

    // POST /api/clients/me/paiement/{reservationId}
    @PostMapping("/me/paiement/{reservationId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> reglerPaiement(
            @PathVariable Long reservationId,
            @Valid @RequestBody PaiementRequest paiementRequest) {

        clientService.reglerPaiement(reservationId, paiementRequest);
        return ResponseEntity.ok().build();
    }
}