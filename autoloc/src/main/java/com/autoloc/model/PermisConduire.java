package com.autoloc.model;

import com.autoloc.enums.categoriePermis;
import com.autoloc.enums.paysEmission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permis_conduire")
public class PermisConduire {

    @Id
    @Column(name = "numero", nullable = false, unique = true)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false)
    private categoriePermis Categorie;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_obtention", nullable = false)
    private Date dateObtention;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_expiration", nullable = false)
    private Date dateExpiration;

    @Enumerated(EnumType.STRING)
    @Column(name = "pays_emission", nullable = false)
    private paysEmission paysEmission;
}