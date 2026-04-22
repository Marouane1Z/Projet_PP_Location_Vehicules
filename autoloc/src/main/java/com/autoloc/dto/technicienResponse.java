package com.autoloc.dto.technicien;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicienResponse {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String specialite;
    private Boolean disponible;

    // ─── Champs supplémentaires utiles ────────────────────────────────────

    private String phone;
    private Boolean actif;
}