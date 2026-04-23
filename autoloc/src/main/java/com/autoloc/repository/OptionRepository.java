package com.autoloc.repository;

import com.autoloc.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    // Chercher une option par son nom
    // ex : findByNom("GPS") → Optional<Option>
    Optional<Option> findByNom(String nom);

    // Vérifier si une option avec ce nom existe déjà
    // Utile avant d'ajouter une option en doublon
    boolean existsByNom(String nom);

    // Chercher toutes les options dont le nom contient un mot clé
    // ex : findByNomContainingIgnoreCase("clim") → [Climatisation]
    List<Option> findByNomContainingIgnoreCase(String keyword);
}