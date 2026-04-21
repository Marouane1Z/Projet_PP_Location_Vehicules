package com.autoloc.model;

import com.autoloc.enums.statutVehicule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicule")
@Inheritance(strategy = InheritanceType.JOINED)  //Heritage JAVA SPRING JPA
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public abstract class Vehicule {

    //Cle primaire ID
    @Id

    //Auto Increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private long id;

    /**
     * Discriminateur JPA : "VOITURE" ou "CAMION"
     * Correspond au champ +String Type du diagramme.
     * insertable=false, updatable=false car géré par @DiscriminatorColumn.
     */

    @Column(name = "type", insertable = false, updatable = false)
    private String type;

    @Column(name = "marque", nullable = false, length = 50)
    private String marque;

    @Column(name = "modele", nullable = false, length = 50)
    private String modele;

    @Column(name = "immatriculation", nullable = false, unique = true, length = 20)
    private String immatriculation;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "prix_par_jour", nullable = false)
    private Double prixParJour;

    @Column(name = "caution", nullable = false)
    private Double caution;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private statutVehicule statut = statutVehicule.DISPONIBLE;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "type_carburant", length = 30)
    private String typeCarburant;

    @Column(name = "type_boite_vitesse", length = 30)
    private String typeBoiteVitesse;

    /**
     * Relation Many-to-Many avec Option.
     * Table pivot : option_vehicule (vehicule_id, option_id)
     * Conforme au diagramme : Vehicule *──* Option
     */

    @ManyToMany
    @JoinTable(
            name = "option_vehicule",
            joinColumns        = @JoinColumn(name = "vehicule_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )

    private List<Option> options = new ArrayList<>();

    // Timestamps techniques (non présents dans le diagramme, bonne pratique)
    // @Column(name = "created_at", updatable = false)
    // private LocalDateTime createdAt;

    // @Column(name = "updated_at")
    // private LocalDateTime updatedAt;

    /** @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = StatutVehicule.DISPONIBLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    */


    //les deux méthodes ci-dessous vont être déplacées vers /Services


    /**
     * Vérifie si le véhicule est actuellement disponible.
     */

    public boolean estDisponible() {
        return statutVehicule.DISPONIBLE.equals(this.statut);
    }

    /**
     * Change le statut du véhicule.
     *
     * @param nouveauStatut le nouveau statut à appliquer
     */

    public void changerStatut(statutVehicule nouveauStatut) {
        this.statut = nouveauStatut;
    }

}