package com.autoloc.model;

import com.autoloc.enums.statutMaintenance;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "ordre_maintenance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdreMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String immatriculation;

    @Column(nullable = false)
    private String typeReparation;

    private String description;

    private LocalDate dateSignal;

    private LocalDate dateResolution;

    private Double coutReparation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private statutMaintenance statut;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "technicien_id")
    private Technicien technicien;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
}