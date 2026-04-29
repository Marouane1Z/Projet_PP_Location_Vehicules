package com.autoloc.dto;

import com.autoloc.enums.userRole;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {
    private String token;
    private userRole role;
    private String email;
    private Long userId;
    private String firstname;
    private String lastname;
}

