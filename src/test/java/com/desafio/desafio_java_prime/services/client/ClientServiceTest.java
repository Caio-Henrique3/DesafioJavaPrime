package com.desafio.desafio_java_prime.services.client;

import com.desafio.desafio_java_prime.controllers.clients.dto.ClientRequestDto;
import com.desafio.desafio_java_prime.controllers.clients.dto.ClientResponseDto;
import com.desafio.desafio_java_prime.exceptions.BusinessException;
import com.desafio.desafio_java_prime.exceptions.NotFoundException;
import com.desafio.desafio_java_prime.models.client.Client;
import com.desafio.desafio_java_prime.repositories.client.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository repository;

    @Test
    void shouldReturnAllClientsPaged() {
        // Arrange
        Pageable pageable = buildPageable();
        Client client = buildClient();
        List<Client> clients = List.of(client);
        Page<Client> clientPage = new PageImpl<>(clients, pageable, clients.size());

        when(repository.findAll(pageable)).thenReturn(clientPage);

        // Act
        var result = clientService.getAllClients(pageable);

        // Assert
        assertNotNull(result);

        assertFalse(result.isEmpty());

        assertEquals(1, result.getContent().size());

        ClientResponseDto dto = result.getContent().getFirst();
        assertEquals(client.getName(), dto.name());
        assertEquals(client.getDocument(), dto.document());
        assertEquals(client.getEmail(), dto.email());
        assertEquals(client.getPhone(), dto.phone());

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnClientById() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        Client client = buildClient();

        when(repository.findById(clientId)).thenReturn(Optional.of(client));

        // Act
        ClientResponseDto result = clientService.getClientById(clientId);

        // Assert
        assertNotNull(result);

        assertEquals(client.getId(), result.id());

        assertInstanceOf(ClientResponseDto.class, result);

        verify(repository, times(1)).findById(clientId);
    }

    @Test
    void shouldThrowNotFoundException() {
        // Arrange
        UUID clientId = UUID.randomUUID();

        when(repository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class, () -> clientService.getClientById(clientId)
        );

        assertEquals("Client not found with id: " + clientId, notFoundException.getMessage());

        verify(repository, times(1)).findById(clientId);
    }

    @Test
    void shouldCreateClient() {
        // Arrange
        Client client = buildClient();
        client.setId(UUID.randomUUID());

        ClientRequestDto clientRequestDto = buildClientRequestDto();

        when(repository.existsByEmail(clientRequestDto.getEmail())).thenReturn(false);
        when(repository.existsByDocument(clientRequestDto.getDocument())).thenReturn(false);
        when(repository.save(any(Client.class))).thenReturn(client);

        // Act
        ClientResponseDto result = clientService.createClient(clientRequestDto);

        // Assert
        assertNotNull(result);

        assertEquals(client.getId(), result.id());
        assertEquals(client.getName(), result.name());
        assertEquals(client.getDocument(), result.document());
        assertEquals(client.getEmail(), result.email());
        assertEquals(client.getPhone(), result.phone());

        verify(repository, times(1)).existsByEmail(clientRequestDto.getEmail());
        verify(repository, times(1)).existsByDocument(clientRequestDto.getDocument());
        verify(repository, times(1)).save(any(Client.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenCreatingClientWithExistingEmail() {
        // Arrange
        ClientRequestDto clientRequestDto = buildClientRequestDto();

        when(repository.existsByEmail(clientRequestDto.getEmail())).thenReturn(true);

        // Act & Assert
        BusinessException businessException = assertThrows(
                BusinessException.class, () -> clientService.createClient(clientRequestDto)
        );

        assertEquals("Client with this email already exists.", businessException.getMessage());

        verify(repository, times(1)).existsByEmail(clientRequestDto.getEmail());
        verify(repository, never()).existsByDocument(any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenCreatingClientWithExistingDocument() {
        // Arrange
        ClientRequestDto clientRequestDto = buildClientRequestDto();

        when(repository.existsByEmail(clientRequestDto.getEmail())).thenReturn(false);
        when(repository.existsByDocument(clientRequestDto.getDocument())).thenReturn(true);

        // Act & Assert
        BusinessException businessException = assertThrows(
                BusinessException.class, () -> clientService.createClient(clientRequestDto)
        );

        assertEquals("Client with this document already exists.", businessException.getMessage());

        verify(repository, times(1)).existsByEmail(clientRequestDto.getEmail());
        verify(repository, times(1)).existsByDocument(clientRequestDto.getDocument());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldUpdateClient() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        Client client = buildClient();
        client.setId(clientId);

        ClientRequestDto clientRequestDto = buildClientRequestDto();

        when(repository.findById(clientId)).thenReturn(Optional.of(client));
        when(repository.existsByEmailAndIdNot(clientRequestDto.getEmail(), clientId)).thenReturn(false);
        when(repository.existsByDocumentAndIdNot(clientRequestDto.getDocument(), clientId)).thenReturn(false);
        when(repository.save(any(Client.class))).thenReturn(client);

        // Act
        ClientResponseDto result = clientService.updateClient(clientId, clientRequestDto);

        // Assert
        assertNotNull(result);

        assertEquals(client.getId(), result.id());
        assertEquals(client.getName(), result.name());
        assertEquals(client.getDocument(), result.document());
        assertEquals(client.getEmail(), result.email());
        assertEquals(client.getPhone(), result.phone());

        verify(repository, times(1)).findById(clientId);
        verify(repository, times(1))
                .existsByEmailAndIdNot(clientRequestDto.getEmail(), clientId);
        verify(repository, times(1))
                .existsByDocumentAndIdNot(clientRequestDto.getDocument(), clientId);
        verify(repository, times(1)).save(any(Client.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentClient() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        ClientRequestDto clientRequestDto = buildClientRequestDto();

        when(repository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class, () -> clientService.updateClient(clientId, clientRequestDto)
        );

        assertEquals("Client not found with id: " + clientId, notFoundException.getMessage());

        verify(repository, times(1)).findById(clientId);
        verify(repository, never()).existsByEmailAndIdNot(any(), any());
        verify(repository, never()).existsByDocumentAndIdNot(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenUpdatingClientWithExistingEmail() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        Client client = buildClient();
        client.setId(clientId);

        ClientRequestDto clientRequestDto = buildClientRequestDto();

        when(repository.findById(clientId)).thenReturn(Optional.of(client));
        when(repository.existsByEmailAndIdNot(clientRequestDto.getEmail(), clientId)).thenReturn(true);

        // Act & Assert
        BusinessException businessException = assertThrows(
                BusinessException.class, () -> clientService.updateClient(clientId, clientRequestDto)
        );

        assertEquals("Client with this email already exists.", businessException.getMessage());

        verify(repository, times(1)).findById(clientId);
        verify(repository, times(1))
                .existsByEmailAndIdNot(clientRequestDto.getEmail(), clientId);
        verify(repository, never()).existsByDocumentAndIdNot(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenUpdatingClientWithExistingDocument() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        Client client = buildClient();
        client.setId(clientId);

        ClientRequestDto clientRequestDto = buildClientRequestDto();

        when(repository.findById(clientId)).thenReturn(Optional.of(client));
        when(repository.existsByEmailAndIdNot(clientRequestDto.getEmail(), clientId)).thenReturn(false);
        when(repository.existsByDocumentAndIdNot(clientRequestDto.getDocument(), clientId)).thenReturn(true);

        // Act & Assert
        BusinessException businessException = assertThrows(
                BusinessException.class, () -> clientService.updateClient(clientId, clientRequestDto)
        );

        assertEquals("Client with this document already exists.", businessException.getMessage());

        verify(repository, times(1)).findById(clientId);
        verify(repository, times(1))
                .existsByEmailAndIdNot(clientRequestDto.getEmail(), clientId);
        verify(repository, times(1))
                .existsByDocumentAndIdNot(clientRequestDto.getDocument(), clientId);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteClient() {
        // Arrange
        UUID clientId = UUID.randomUUID();

        when(repository.findById(clientId)).thenReturn(Optional.of(buildClient()));

        // Act
        clientService.deleteClient(clientId);

        // Assert
        verify(repository).deleteById(clientId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentClient() {
        // Arrange
        UUID clientId = UUID.randomUUID();

        when(repository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class, () -> clientService.deleteClient(clientId)
        );

        assertEquals("Client not found with id: " + clientId, notFoundException.getMessage());

        verify(repository, times(1)).findById(clientId);
        verify(repository, never()).delete(any(Client.class));
    }

    private Pageable buildPageable() {
        return PageRequest.of(0, 10);
    }

    private Client buildClient() {
        return Client.builder()
                .name("Test Client")
                .document("12345678901")
                .email("teste@teste.com")
                .phone("1234567890")
                .build();
    }

    private ClientRequestDto buildClientRequestDto() {
        return ClientRequestDto.builder()
                .name("Test Client")
                .document("12345678901")
                .email("teste@teste.com")
                .phone("1234567890")
                .build();
    }

}