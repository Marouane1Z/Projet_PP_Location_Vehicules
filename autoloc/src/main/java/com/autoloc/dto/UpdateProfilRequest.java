package com.autoloc.dto;

<<<<<<< HEAD
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class UpdateProfilRequest {

    @NotBlank(message = "Prénom obligatoire")
    private String firstname;

    @NotBlank(message = "Nom obligatoire")
    private String lastname;

    private String phone;
    private String address;
}
=======
public class UpdateProfilRequest {
}
>>>>>>> develop
