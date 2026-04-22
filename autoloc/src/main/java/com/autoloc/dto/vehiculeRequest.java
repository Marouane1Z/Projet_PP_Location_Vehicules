package com.autoloc.dto.vehicule;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
public class VehiculeRequest {

    // ─── Champs communs (image) ───────────────────────────────────────────

    @NotBlank(message = "Saisissez VOITURE ou CAMION")
    @Pattern(regexp = "VOITURE|CAMION", message = "Le type doit être VOITURE ou CAMION")
    private String type;

    @NotEmpty(message = "Champ Marque vide")
    //@Size(max = 50, message = "La marque ne doit pas dépasser 50 caractères")
    private String marque;

    @NotEmpty(message = "Champ Modele vide")
    //@Size(max = 50, message = "Le modèle ne doit pas dépasser 50 caractères")
    private String modele;

    @NotBlank(message = "Entrez Immatriculation")
    @Size(max = 20, message = "L'immatriculation ne doit pas dépasser 20 caractères")
    private String immatriculation;

    /*@NotNull(message = "L'année est obligatoire")
    @Min(value = 1900, message = "L'année doit être supérieure à 1900")
    @Max(value = 2100, message = "L'année doit être inférieure à 2100")
    private Integer annee;
    **/


    /*@NotNull(message = "Saisissez un prix")
    @Positive(message = "Le prix doit être positif")
    private Double prixParJour;
    **/

    /*@NotNull(message = "La caution est obligatoire")
    @PositiveOrZero(message = "La caution doit être positive")
    private Double caution;
    **/


    @Size(max = 30)
    private String typeCarburant;

    @Size(max = 30)
    private String typeBoite;

    private String image;


    // ─── Champs Voiture (utilisés si type = VOITURE) ──────────────────────

    /*@Positive(message = "Saisissez un nombre positif")
    private Integer nbPortes;
    **/

    /*@Positive(message = "Saisissez un nombre positif")
    private Integer nbPlaces;
    **/


    @Size(max = 30)
    private String categorie;


    // ─── Champs Camion (utilisés si type = CAMION) ────────────────────────

    /*@Positive(message = "Entrez valeur positif")
    private Double tonnage;
    **/

    /*@Positive(message = "Entrez valeur positif")
    private Double volume;
    **/

    /*@Positive(message = "Entrez valeur positive")
    private Double longueur;

    private Boolean elevator = false;
    **/

    // ─── Options à associer

    private List<Long> optionIds;
}