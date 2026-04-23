package com.autoloc.dto;

import com.autoloc.enums.categoriePermis;
import com.autoloc.enums.paysEmission;
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

    private String          permisNumero;
    private categoriePermis permisCategorie;
    private LocalDate       permisObtention;
    private LocalDate       permisExpiration;
    private paysEmission    permisPaysEmission;
}