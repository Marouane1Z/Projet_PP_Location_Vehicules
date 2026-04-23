package com.autoloc.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class LoginRequest {

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Password obligatoire")
    private String password;

}
