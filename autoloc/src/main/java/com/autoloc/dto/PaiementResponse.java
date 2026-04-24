package com.autoloc.dto;

import com.autoloc.enums.modePaiement;
import com.autoloc.enums.statutPaiement;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaiementResponse {
    private Long id;
    private Double montant;
    private statutPaiement statut;
    private LocalDate datePaiement;
    private modePaiement modePaiement;
}

