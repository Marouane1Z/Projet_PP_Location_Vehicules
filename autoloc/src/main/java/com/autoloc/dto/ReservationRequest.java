package com.autoloc.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {
    private Long vehiculeId;
    private LocalDate dateDebut;
    private LocalDate dateFin;

}
