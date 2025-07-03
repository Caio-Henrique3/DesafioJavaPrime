package com.desafio.desafio_java_prime.repositories.client;

import com.desafio.desafio_java_prime.models.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    boolean existsByEmail(String email);

    boolean existsByDocument(String document);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByDocumentAndIdNot(String document, UUID id);

}
