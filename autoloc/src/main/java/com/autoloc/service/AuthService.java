package com.autoloc.service;

import com.autoloc.dto.JwtResponse;
import com.autoloc.dto.LoginRequest;
import com.autoloc.dto.RegisterRequest;
import com.autoloc.dto.UpdateProfilRequest;
import com.autoloc.enums.userRole;
import com.autoloc.model.Client;
import com.autoloc.model.User;
import com.autoloc.repository.UserRepository;
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
        return new JwtResponse(token,user.getRole(), user.getEmail());
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

        return new JwtResponse(token, client.getRole(), client.getEmail());
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
    // 4. CHANGER MOT DE PASSE — tout utilisateur connecté
    public void changePassword(Long id, ChangePasswordRequest request)

    // 5. CRÉER ADMIN — SuperAdmin seulement
    public void createAdmin(RegisterRequest request)

    // 6. DÉSACTIVER COMPTE — Admin seulement
    public void desactiverCompte(Long id)
}