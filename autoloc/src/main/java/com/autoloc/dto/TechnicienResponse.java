package com.autoloc.dto;

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
    private String password;
    private String role;
    private String specialite;
    private Boolean disponible;

    // ─── Champs supplémentaires utiles ────────────────────────────────────

    private String phone;
    private Boolean actif;
}