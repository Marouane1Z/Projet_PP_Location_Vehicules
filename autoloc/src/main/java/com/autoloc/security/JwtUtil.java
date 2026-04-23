package com.autoloc.security;

import com.autoloc.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Générer la clé depuis le secret
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ─── GÉNÉRER LE TOKEN ────────────────────────────────
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ─── EXTRAIRE L'EMAIL ────────────────────────────────
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ─── EXTRAIRE LE ROLE ────────────────────────────────
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ─── VALIDER LE TOKEN ────────────────────────────────
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ─── EXTRAIRE LES CLAIMS ─────────────────────────────
    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(getKey())
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}