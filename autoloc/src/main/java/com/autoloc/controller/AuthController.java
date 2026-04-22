package com.autoloc.controller;

import com.autoloc.dto.*;
import com.autoloc.model.User;
import com.autoloc.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request)
    {
            return ResponseEntity.ok(authService.register(request)) ;
    }
    @PutMapping("/profil")
    public ResponseEntity<String> updateProfil(
            @Valid @RequestBody UpdateProfilRequest request,
            @AuthenticationPrincipal User user) {

        authService.updateProfil(user.getId(), request);
        return ResponseEntity.ok("Profil mis à jour avec succès");
    }


}