package com.desafio.desafio_java_prime.services.client;

import com.desafio.desafio_java_prime.controllers.clients.dto.ClientRequestDto;
import com.desafio.desafio_java_prime.controllers.clients.dto.ClientResponseDto;
import com.desafio.desafio_java_prime.exceptions.BusinessException;
import com.desafio.desafio_java_prime.exceptions.NotFoundException;
import com.desafio.desafio_java_prime.models.client.Client;
import com.desafio.desafio_java_prime.repositories.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    public Page<ClientResponseDto> getAllClients(Pageable pageable) {
        return repository.findAll(pageable)
                .map(ClientResponseDto::fromEntity);
    }

    public Client getClient(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
    }

    public ClientResponseDto getClientById(UUID id) {
        return ClientResponseDto.fromEntity(getClient(id));
    }

    @Transactional
    public ClientResponseDto createClient(ClientRequestDto clientDto) {
        if (repository.existsByEmail(clientDto.getEmail())) {
            throw new BusinessException("Client with this email already exists.");
        }

        if (repository.existsByDocument(clientDto.getDocument())) {
            throw new BusinessException("Client with this document already exists.");
        }

        Client client = Client.builder()
                .name(clientDto.getName())
                .email(clientDto.getEmail())
                .phone(clientDto.getPhone())
                .document(clientDto.getDocument())
                .build();

        return ClientResponseDto.fromEntity(repository.save(client));
    }

    @Transactional
    public ClientResponseDto updateClient(UUID id, ClientRequestDto clientDto) {
        Client client = getClient(id);

        if (repository.existsByEmailAndIdNot(clientDto.getEmail(), id)) {
            throw new BusinessException("Client with this email already exists.");
        }

        if (repository.existsByDocumentAndIdNot(clientDto.getDocument(), id)) {
            throw new BusinessException("Client with this document already exists.");
        }

        client.setName(clientDto.getName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setDocument(clientDto.getDocument());

        return ClientResponseDto.fromEntity(repository.save(client));
    }

    @Transactional
    public void deleteClient(UUID id) {
        getClient(id);

        repository.deleteById(id);
    }

}
