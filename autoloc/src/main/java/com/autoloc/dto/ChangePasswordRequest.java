package com.autoloc.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Ancien password obligatoire")
    private String ancienPassword;

    @NotBlank(message = "Nouveau password obligatoire")
    @Size(min = 6, message = "Password minimum 6 caractères")
    private String nouveauPassword;
}