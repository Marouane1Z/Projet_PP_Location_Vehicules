package com.autoloc.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicule_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    /**
     * Côté inverse de la relation M:N.
     * mappedBy = "options" fait référence au champ dans Vehicule.java.
     */

    @ManyToMany(mappedBy = "options")
    private List<Vehicule> vehicules = new ArrayList<>();
}