package com.autoloc.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private String adress;
    private LocalDate dateNaissance;

    // Permis
    private String permisNumero;
    private String permisCategorie;
    private LocalDate permisExpiration;
}