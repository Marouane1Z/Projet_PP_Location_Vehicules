package com.autoloc.dto;

import com.autoloc.enums.statutReservation;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private Long id;
    private Long vehiculeId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double montant;
    private statutReservation statut;
    private LocalDate dateCreation;
}
