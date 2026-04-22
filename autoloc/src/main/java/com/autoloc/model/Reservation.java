package com.autoloc.model;

import com.autoloc.enums.statutReservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_debut", nullable = false)
    private Date dateDebut;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_fin", nullable = false)
    private Date dateFin;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_retour")
    private Date dateRetour;

    @Column(nullable = false)
    private Double montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_reservation", nullable = false)
    private statutReservation statutReservation;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

}
