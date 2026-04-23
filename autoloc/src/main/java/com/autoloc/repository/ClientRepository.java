package com.autoloc.repository;

import com.autoloc.enums.userRole;
import com.autoloc.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Client> findByActifTrue();

    List<Client> findByRole(userRole role);
}