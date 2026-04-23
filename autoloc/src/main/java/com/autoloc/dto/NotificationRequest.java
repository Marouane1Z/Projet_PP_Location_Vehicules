package com.autoloc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO entrant — données nécessaires pour créer une notification.
 *
 * Utilisé dans : POST /api/notifications
 * Appelé aussi directement par les autres Services (VehiculeService,
 * MaintenanceService...) sans passer par le Controller.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    //@NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    private Long userId;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le message est obligatoire")
    private String message;
}