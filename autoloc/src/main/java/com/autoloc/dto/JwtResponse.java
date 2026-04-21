package com.autoloc.dto;

import com.autoloc.enums.userRole;
import lombok.*;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private userRole role;
    private String email;
}