package com.autoloc.repository;

import com.autoloc.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Récupérer les clients où admin = true (hérité de User)
    List<Client> findByAdminTrue();

    // Récupérer un client par email (hérité de User))
    Optional<Client> findByEmail(String email);
}