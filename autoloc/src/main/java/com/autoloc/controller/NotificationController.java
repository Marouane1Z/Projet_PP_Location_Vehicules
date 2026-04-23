package com.autoloc.controller;

import com.autoloc.dto.NotificationRequest;
import com.autoloc.dto.NotificationResponse;
import com.autoloc.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * NotificationController — basé sur NotificationService.java
 *
 * Méthodes disponibles dans le Service :
 *   envoyer(NotificationRequest), findByUtilisateurId, marquerLue
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ─── POST — envoyer ───────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<NotificationResponse> envoyer(
            @RequestBody @Valid NotificationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationService.envoyer(request));
    }

    // ─── GET — findByUtilisateurId ────────────────────────────────────────
    // ex : GET /api/notifications/user/3

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.findByUtilisateurId(userId));
    }

    // ─── PATCH — marquerLue ───────────────────────────────────────────────
    // ex : PATCH /api/notifications/5/lue

    @PatchMapping("/{id}/lue")
    public ResponseEntity<Void> marquerLue(@PathVariable Long id) {
        notificationService.marquerLue(id);
        return ResponseEntity.noContent().build();
    }
}