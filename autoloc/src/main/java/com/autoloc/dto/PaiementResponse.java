package com.autoloc.dto;

import com.autoloc.enums.modePaiement;
import com.autoloc.enums.statutPaiement;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaiementResponse {
    private Long id;
    private Double montant;
    private statutPaiement statut;
    private LocalDateTime datePaiement;
    private modePaiement modePaiement;
}

