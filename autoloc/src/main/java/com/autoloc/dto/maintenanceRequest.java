package com.autoloc.dto.maintenance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class MaintenanceRequest {

    @NotNull(message = "L'identifiant du véhicule est obligatoire")
    private Long vehiculeId;

    // technicienId peut être null si le technicien n'est pas encore assigné
    private Long technicienId;

    @NotBlank(message = "Le type de réparation est obligatoire")
    private String typeReparation;

    private String description;
}