package com.autoloc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class TechnicienRequest {

    // ─── Champs de l'image ────────────────────────────────────────────────

    @NotBlank(message = "Le prénom est obligatoire")
    //@Size(max = 50)
    private String firstname;

    @NotBlank(message = "Le nom est obligatoire")
    //@Size(max = 50)
    private String lastname;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    //@NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    private String role;

    private Boolean actif;

    @NotBlank(message = "La spécialité est obligatoire")
    @Size(max = 100)
    private String specialite;

    private Boolean disponible;
}