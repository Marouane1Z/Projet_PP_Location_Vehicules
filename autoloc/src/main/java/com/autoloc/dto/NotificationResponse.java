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
}

