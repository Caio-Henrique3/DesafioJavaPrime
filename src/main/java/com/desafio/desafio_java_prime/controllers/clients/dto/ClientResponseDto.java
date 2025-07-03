package com.desafio.desafio_java_prime.controllers.clients.dto;

import com.desafio.desafio_java_prime.models.client.Client;

import java.util.UUID;

public record ClientResponseDto(
        UUID id,
        String name,
        String document,
        String email,
        String phone
) {
    public static ClientResponseDto fromEntity(Client client) {
        return new ClientResponseDto(
                client.getId(),
                client.getName(),
                client.getDocument(),
                client.getEmail(),
                client.getPhone()
        );
    }
}
