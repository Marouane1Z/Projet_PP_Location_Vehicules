package com.autoloc.dto;

import com.autoloc.enums.modePaiement;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class PaiementRequest {

    @NotNull(message = "Reservation obligatoire")
    private Long reservationId;

    @NotNull(message = "Montant obligatoire")
    private Double montant;

    @NotNull(message = "Mode de paiement obligatoire")
    private modePaiement modePaiement;
}

