package com.autoloc.dto;

import com.autoloc.enums.statutMaintenance;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceResponse {

    // ─── Champs de l'image ────────────────────────────────────────────────

    private Long id;
    private String typeReparation;
    private statutMaintenance statut;
    private LocalDate dateSignal;
    private LocalDate dateResolution;
    private Double coutReparation;

    // ─── Champs supplémentaires utiles ────────────────────────────────────

    private String description;

    // Infos du véhicule concerné
    private Long vehiculeId;
    private String vehiculeMarque;
    private String vehiculeModele;
    private String vehiculeImmatriculation;

    // Infos du technicien assigné
    private Long technicienId;
    private String technicienNom;
    private String technicienPrenom;
}