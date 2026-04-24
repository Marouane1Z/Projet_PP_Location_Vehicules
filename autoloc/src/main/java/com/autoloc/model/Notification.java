package com.autoloc.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import java.time.*;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "titre", nullable = false, length= 100)
    private String titre;

    @Column(name = "message")
    private String Message;

    @Column(name = "dateEnvoi")
    private LocalDateTime dateEnvoi;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;

}