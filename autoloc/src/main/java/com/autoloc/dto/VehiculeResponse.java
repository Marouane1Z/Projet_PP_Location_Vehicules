package com.autoloc.dto;

import com.autoloc.enums.statutVehicule;
import lombok.*;

import java.util.List;

/**
 * DTO sortant — données renvoyées au frontend après lecture d'un véhicule.
 *
 * Champs définis dans l'image :
 *   id, marque, modele, immatriculation, prixParJour, statut, options
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculeResponse {

    // ─── Champs de l'image ────────────────────────────────────────────────

    private Long id;
    private String marque;
    private String modele;
    private String immatriculation;
    private Double prixParJour;
    private statutVehicule statut;

    // Champs supplémentaires utiles

    private String type;           // VOITURE ou CAMION
    private Double caution;
    private Integer annee;
    private String typeCarburant;
    private String typeBoite;
    private String image;

    //Champs Voiture (null si CAMION)

    private Integer nbPortes;
    private Integer nbPlaces;
    private String categorie;

    // Champs Camion (null si VOITURE)

    private Double tonnage;
    private Double volume;
    private Double longueur;
    private Boolean elevator;

    // Options

    private List<OptionResponse> options;

    // DTO imbriqué pour les options

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResponse {
        private Long id;
        private String nom;
    }
}
