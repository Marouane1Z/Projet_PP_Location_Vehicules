package com.autoloc.service;

import com.autoloc.dto.*;
import com.autoloc.enums.userRole;
import com.autoloc.model.Admin;
import com.autoloc.model.Client;
import com.autoloc.model.User;
import com.autoloc.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoloc.security.JwtUtil;



@Service
@RequiredArgsConstructor
@Transactional

// SeConnecter
public class AuthService{
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final JwtUtil jwtUtil ;
    // chercher l'utilisateur par mail saisie

    public JwtResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException("User not found")) ;
        //vERIFIER LE MOT DE PASSE /
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Wrong password");
        }
        // gENERER LE TOKEN JWT
        String token = jwtUtil.generateToken(user) ;
        // retourner l'utilisateur
        return new JwtResponse(token, user.getRole(), user.getEmail(), user.getId(), user.getFirstname(), user.getLastname());
        }

    // Créer un compte Client

    public JwtResponse register(RegisterRequest request){
        // verfifier si l'email n'existe pas deja
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email deja utilisé");
        }
        // CREER LE CLIENT
        Client client = new Client() ;
        client.setFirstname(request.getFirstname());
        client.setLastname(request.getLastname());
        client.setEmail(request.getEmail()) ;
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setAddress(request.getAddress());
        client.setPhone(request.getPhone());
        client.setActif(true);
        client.setPermisConduire(request.getPermisConduire());
        client.setRole(userRole.CLIENT);

        userRepository.save(client) ;
        String token = jwtUtil.generateToken(client) ;

        return new JwtResponse(token, client.getRole(), client.getEmail(), client.getId(), client.getFirstname(), client.getLastname());
    }


    // Modifier profile
    public void updateProfil(Long id, UpdateProfilRequest request) {

        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("Utilisateur introuvable")) ;

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        userRepository.save(user) ;

    }
    public void changePassword(Long id, ChangePasswordRequest request) {

        // 1. Trouver l'utilisateur
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 2. Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getAncienPassword(), user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        // 3. Vérifier que le nouveau mot de passe est différent
        if (passwordEncoder.matches(request.getNouveauPassword(), user.getPassword())) {
            throw new RuntimeException("Le nouveau mot de passe doit être différent");
        }

        // 4. Encoder et sauvegarder
        user.setPassword(passwordEncoder.encode(request.getNouveauPassword()));
        userRepository.save(user);
    }

    public void createAdmin(RegisterRequest request) {

        // 1. Vérifier que l'email n'existe pas déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // 2. Créer l'admin
        Admin admin = new Admin();
        admin.setFirstname(request.getFirstname());
        admin.setLastname(request.getLastname());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setPhone(request.getPhone());
        admin.setAddress(request.getAddress());
        admin.setRole(userRole.ADMIN);
        admin.setActif(true);

        // 3. Sauvegarder
        userRepository.save(admin);
    }
}