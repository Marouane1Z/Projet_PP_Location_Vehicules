package com.autoloc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "voiture")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("VOITURE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Voiture extends Vehicule {

    @Column(name = "nb_portes")
    private Integer nbPortes;

    @Column(name = "nb_places")
    private Integer nbPlaces;

    @Column(name = "categorie", length = 30)
    private String categorie;

    /**
     * Une voiture standard ne nécessite qu'un permis B.
     * Retourne toujours false : pas de permis spécial requis.
     */

}
