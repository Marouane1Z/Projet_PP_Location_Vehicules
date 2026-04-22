package com.autoloc.dto;

<<<<<<< HEAD
import com.autoloc.model.PermisConduire;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class RegisterRequest {

    @NotBlank(message = "Prénom obligatoire")
    private String firstname;

    @NotBlank(message = "Nom obligatoire")
    private String lastname;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Password obligatoire")
    @Size(min = 6, message = "Password minimum 6 caractères")
    private String password;

    private PermisConduire permisConduire;
    private String phone;
    private String address;
}
=======
public class RegisterRequest {
}
>>>>>>> develop
