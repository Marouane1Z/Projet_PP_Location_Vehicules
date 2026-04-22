package com.autoloc.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

    private Long id;

    private String firstname;
    private String lastname;
    private String email;
    private String phone;

    private String permisCategorie;

    private boolean actif;
}