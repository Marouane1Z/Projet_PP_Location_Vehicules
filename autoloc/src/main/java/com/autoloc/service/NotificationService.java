package com.autoloc.service;

import com.autoloc.dto.NotificationRequest;
import com.autoloc.dto.NotificationResponse;
import com.autoloc.model.Notification;
import com.autoloc.model.User;
import com.autoloc.repository.NotificationRepository;
import com.autoloc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;

    // ─── envoyer ──────────────────────────────────────────────────────────

    public NotificationResponse envoyer(NotificationRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur introuvable avec l'id : " + request.getUserId()
                ));

        Notification notif = new Notification();
        notif.setTitre(request.getTitre());
        notif.setMessage(request.getMessage());
        notif.setU(user);
        notif.setDateEnvoi(LocalDateTime.now());

        return toResponse(notificationRepository.save(notif));
    }

   /* public NotificationResponse envoyer(NotificationRequest request) {
        return envoyer(
                request.getUserId(),
                request.getTitre(),
                request.getMessage()
        );
    }**/

    // findByUtilisateurId pour afficher une page qui contient les notifications des utilisateurs

    @Transactional(readOnly = true)
    public List<NotificationResponse> findByUtilisateurId(Long userId) {
        return notificationRepository
                .findByUserIdOrderByDateEnvoiDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // MarquerLue

    public void marquerLue(Long notifId) {
        Notification notif = notificationRepository.findById(notifId)
                .orElseThrow(() -> new RuntimeException(
                        "Notification introuvable avec l'id : " + notifId
                ));
        // notif.setLue(true); ← ajouter Boolean lue dans Notification.java
        notificationRepository.save(notif);
    }

    // ─── toResponse : entité → DTO ────────────────────────────────────────

    public NotificationResponse toResponse(Notification notif) {
        NotificationResponse r = new NotificationResponse();
        r.setId(notif.getId());
        r.setTitre(notif.getTitre());
        r.setMessage(notif.getMessage());
        r.setDateEnvoi(notif.getDateEnvoi());

        if (notif.getU() != null) {
            r.setUserId(notif.getU().getId());
            r.setUserFirstname(notif.getU().getFirstname());
            r.setUserLastname(notif.getU().getLastname());
        }

        return r;
    }
}