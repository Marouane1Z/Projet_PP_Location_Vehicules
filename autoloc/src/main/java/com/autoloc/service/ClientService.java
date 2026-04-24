package com.autoloc.service;

import com.autoloc.dto.ClientRequest;
import com.autoloc.dto.ClientResponse;
import com.autoloc.dto.ReservationRequest;
import com.autoloc.dto.ReservationResponse;
import com.autoloc.exception.UserNotFoundException;
import com.autoloc.model.Client;
import com.autoloc.model.PermisConduire;
import com.autoloc.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository   clientRepository;
    private final ReservationService reservationService;
    private final PaiementService    paiementService;

    // findAll [ADMIN]
    public List<ClientResponse> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // findById [ADMIN]
    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToResponse(client);
    }

    // createClient [ADMIN]
    public ClientResponse createClient(ClientRequest request) {

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + request.getEmail());
        }

        PermisConduire permis = new PermisConduire();
        permis.setNumero(request.getPermisNumero());
        permis.setCategorie(request.getPermisCategorie());           // ✅ enum → enum
        permis.setDateObtention(toDate(request.getPermisObtention())); // ✅ LocalDate → Date
        permis.setDateExpiration(toDate(request.getPermisExpiration())); // ✅ LocalDate → Date
        permis.setPaysEmission(request.getPermisPaysEmission());     // ✅ manquait

        // Construire le client
        Client client = new Client();
        client.setFirstname(request.getFirstname());
        client.setLastname(request.getLastname());
        client.setEmail(request.getEmail());
        client.setPassword(request.getPassword());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAdress());
        client.setPermisConduire(permis);
        client.setActif(true);

        return mapToResponse(clientRepository.save(client));
    }

    // modifierInformations [CLIENT / ADMIN]
    public ClientResponse modifierInformations(Long clientId, ClientRequest request) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new UserNotFoundException(clientId));

        if (request.getFirstname()    != null) client.setFirstname(request.getFirstname());
        if (request.getLastname()     != null) client.setLastname(request.getLastname());
        if (request.getPhone()        != null) client.setPhone(request.getPhone());
        if (request.getAdress()       != null) client.setAddress(request.getAdress());

        if (request.getPermisNumero()       != null ||
                request.getPermisCategorie()    != null ||
                request.getPermisObtention()    != null ||
                request.getPermisExpiration()   != null ||
                request.getPermisPaysEmission() != null) {

            PermisConduire permis = client.getPermisConduire();
            if (permis == null) permis = new PermisConduire();

            if (request.getPermisNumero()    != null)
                permis.setNumero(request.getPermisNumero());

            if (request.getPermisCategorie() != null)
                permis.setCategorie(request.getPermisCategorie()); // ✅ enum → enum

            if (request.getPermisObtention() != null)
                permis.setDateObtention(toDate(request.getPermisObtention())); // ✅

            if (request.getPermisExpiration() != null)
                permis.setDateExpiration(toDate(request.getPermisExpiration())); // ✅

            if (request.getPermisPaysEmission() != null)
                permis.setPaysEmission(request.getPermisPaysEmission()); // ✅

            client.setPermisConduire(permis);
        }

        return mapToResponse(clientRepository.save(client));
    }

    // desactiverClient [ADMIN]
    public void desactiverClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new UserNotFoundException(clientId));
        client.setActif(false);
        clientRepository.save(client);
    }

    // supprimerClient [ADMIN]
    public void supprimerClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new UserNotFoundException(clientId);
        }
        clientRepository.deleteById(clientId);
    }

    // reserverVehicule [CLIENT]
    public ReservationResponse reserverVehicule(Long clientId, ReservationRequest request) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new UserNotFoundException(clientId));

        if (!Boolean.TRUE.equals(client.getActif())) {
            throw new RuntimeException("Compte désactivé — impossible de réserver");
        }

        return reservationService.createReservation(clientId, request);
    }

    // afficherVehicules [PUBLIC]
    public List<PermisConduire> afficherVehicules() {
        return clientRepository.findAll()
                .stream()
                .map(Client::getPermisConduire)
                .toList();
    }

    // afficherReservations [CLIENT]
    public List<ReservationResponse> afficherReservations(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new UserNotFoundException(clientId);
        }
        return reservationService.getReservationsByClient(clientId);
    }

    // annulerReservations [CLIENT]
    public void annulerReservations(Long clientId, Long reservationId) {

        boolean appartientAuClient = reservationService
                .getReservationsByClient(clientId)
                .stream()
                .anyMatch(r -> r.getId().equals(reservationId));

        if (!appartientAuClient) {
            throw new RuntimeException("Cette réservation ne vous appartient pas");
        }

        reservationService.annuler(reservationId);
    }

    // modifierReservations [CLIENT]
    public ReservationResponse modifierReservations(Long clientId,
                                                    Long reservationId,
                                                    ReservationRequest request) {

        boolean appartientAuClient = reservationService
                .getReservationsByClient(clientId)
                .stream()
                .anyMatch(r -> r.getId().equals(reservationId));

        if (!appartientAuClient) {
            throw new RuntimeException("Cette réservation ne vous appartient pas");
        }

        return reservationService.modifier(reservationId, request);
    }


    // reglerPaiement [CLIENT]

    public void reglerPaiement(Long reservationId,
                               com.autoloc.dto.PaiementRequest paiementRequest) {
        paiementService.effectuerPaiement(reservationId, paiementRequest);
    }



    private Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // MAPPER entité → DTO
    private ClientResponse mapToResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .firstname(client.getFirstname())
                .lastname(client.getLastname())
                .email(client.getEmail())
                .phone(client.getPhone())
                .permisCategorie(
                        client.getPermisConduire() != null
                                ? client.getPermisConduire().getCategorie().name()
                                : null
                )
                .actif(Boolean.TRUE.equals(client.getActif()))
                .build();
    }
}