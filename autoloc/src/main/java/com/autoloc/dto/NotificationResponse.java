package com.autoloc.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private String titre;
    private String message;
    private LocalDateTime dateEnvoi;

    // Infos de l'utilisateur destinataire
    private Long userId;
    private String userFirstname;
    private String userLastname;

}

