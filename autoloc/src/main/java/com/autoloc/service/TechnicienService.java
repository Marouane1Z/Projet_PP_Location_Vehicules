package com.autoloc.service;

import com.autoloc.dto.technicien.TechnicienRequest;
import com.autoloc.dto.technicien.TechnicienResponse;
import com.autoloc.enums.userRole;
import com.autoloc.exception.TechnicienNotFoundException;
import com.autoloc.model.Technicien;
import com.autoloc.repository.TechnicienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Technicien — logique métier liée aux techniciens.
 *
 * Règles métier :
 *   - Un technicien est créé UNIQUEMENT par l'Admin (jamais auto-inscription)
 *   - Email unique dans tout le système (hérité de User)
 *   - Mot de passe hashé avec BCrypt avant sauvegarde
 *   - Disponible = true par défaut à la création
 *   - Role = Mechanicien automatiquement à la création
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TechnicienService {

    private final TechnicienRepository technicienRepository;
    private final PasswordEncoder      passwordEncoder;

    // ─── CRÉER (Admin uniquement) ─────────────────────────────────────────

    /**
     * Crée un nouveau compte technicien.
     * Basé sur les champs de Technicien.java + User.java :
     *   - firstname, lastname, email, password (User)
     *   - specialite, disponible (Technicien)
     */
    public TechnicienResponse creer(TechnicienRequest request) {

        // Règle métier : email unique (hérité de User — unique = true)
        if (technicienRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Email déjà utilisé : " + request.getEmail()
            );
        }

        Technicien technicien = new Technicien();

        // Champs hérités de User.java
        technicien.setFirstname(request.getFirstname());
        technicien.setLastname(request.getLastname());
        technicien.setEmail(request.getEmail());
        technicien.setPassword(passwordEncoder.encode(request.getPassword()));
        technicien.setRole(userRole.Mechanicien);  // role fixé automatiquement
        technicien.setActif(true);                 // actif par défaut

        // Champs propres de Technicien.java
        technicien.setSpecialite(request.getSpecialite());
        technicien.setDisponible(true);            // disponible par défaut

        return toResponse(technicienRepository.save(technicien));
    }

    // ─── LIRE ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TechnicienResponse findById(Long id) {
        return toResponse(
                technicienRepository.findById(id)
                        .orElseThrow(() -> new TechnicienNotFoundException(id))
        );
    }

    @Transactional(readOnly = true)
    public List<TechnicienResponse> findAll() {
        return technicienRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnicienResponse> findDisponibles() {
        return technicienRepository.findByDisponibleTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnicienResponse> findBySpecialite(String specialite) {
        return technicienRepository.findBySpecialite(specialite)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TechnicienResponse> findByNom(String lastname) {
        return technicienRepository.findByLastname(lastname)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── MODIFIER ─────────────────────────────────────────────────────────

    public TechnicienResponse modifier(Long id, TechnicienRequest request) {
        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new TechnicienNotFoundException(id));

        // On peut modifier prénom, nom, spécialité
        // Email et password ne se modifient pas ici (endpoints dédiés)
        technicien.setFirstname(request.getFirstname());
        technicien.setLastname(request.getLastname());
        technicien.setSpecialite(request.getSpecialite());

        return toResponse(technicienRepository.save(technicien));
    }

    // ─── CHANGER DISPONIBILITÉ ────────────────────────────────────────────

    /**
     * Appelé automatiquement par MaintenanceService :
     *   - disponible = false quand un ordre lui est assigné
     *   - disponible = true quand il clôture la réparation
     */
    public void changerDisponibilite(Long id, Boolean disponible) {
        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new TechnicienNotFoundException(id));
        technicien.setDisponible(disponible);
        technicienRepository.save(technicien);
    }

    // ─── DÉSACTIVER ───────────────────────────────────────────────────────

    /**
     * Désactive le compte sans supprimer (actif = false).
     * Le technicien ne peut plus se connecter mais ses données sont conservées.
     */
    public void desactiver(Long id) {
        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new TechnicienNotFoundException(id));
        technicien.setActif(false);
        technicienRepository.save(technicien);
    }

    // ─── SUPPRIMER ────────────────────────────────────────────────────────

    public void supprimer(Long id) {
        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new TechnicienNotFoundException(id));

        // Règle métier : ne pas supprimer un technicien non disponible
        // (il a peut-être un ordre EN_COURS)
        if (!technicien.getDisponible()) {
            throw new IllegalStateException(
                    "Impossible de supprimer un technicien avec une réparation en cours"
            );
        }

        technicienRepository.delete(technicien);
    }

    // ─── toResponse : entité → DTO ────────────────────────────────────────

    /**
     * Convertit Technicien en TechnicienResponse.
     * Champs de l'image : id, firstname, lastname, email, specialite, disponible
     * Champs hérités de User : phone, actif
     */
    public TechnicienResponse toResponse(Technicien technicien) {
        TechnicienResponse r = new TechnicienResponse();

        // Champs hérités de User.java
        r.setId(technicien.getId());
        r.setFirstname(technicien.getFirstname());
        r.setLastname(technicien.getLastname());
        r.setEmail(technicien.getEmail());
        r.setPhone(technicien.getPhone());
        r.setActif(technicien.getActif());

        // Champs propres de Technicien.java
        r.setSpecialite(technicien.getSpecialite());
        r.setDisponible(technicien.getDisponible());

        return r;
    }
}