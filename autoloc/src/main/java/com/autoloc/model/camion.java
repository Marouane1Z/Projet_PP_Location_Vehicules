package com.autoloc.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Classe Camion — conforme au diagramme de classes.
 *
 * Champs du diagramme :
 *   +Double Tonnage
 *   +Double Volume
 *   +Double Longueur
 *   +Boolean Elevator
 *
 * Stratégie JPA JOINED :
 *   - table "camion" avec une seule colonne "id"
 *   - cet "id" est à la fois PK et FK → vehicule.id
 *   - PAS d'AUTO_INCREMENT ici : JPA utilise l'id généré par vehicule
 */

@Entity
@Table(name = "camion")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("CAMION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Camion extends Vehicule {

    @Column(name = "tonnage")
    private Double tonnage;

    @Column(name = "volume")
    private Double volume;

    @Column(name = "longueur")
    private Double longueur;

    @Column(name = "elevator", nullable = false)
    private Boolean elevator;

}
