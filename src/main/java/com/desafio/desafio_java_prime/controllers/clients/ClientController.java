package com.desafio.desafio_java_prime.controllers.clients;

import com.desafio.desafio_java_prime.controllers.clients.dto.ClientRequestDto;
import com.desafio.desafio_java_prime.controllers.clients.dto.ClientResponseDto;
import com.desafio.desafio_java_prime.services.client.ClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("clients")
@RequiredArgsConstructor
@Tag(name = "Clients")
public class ClientController {

    private final ClientService service;

    @GetMapping
    public ResponseEntity<Page<ClientResponseDto>> getAllClients(
            @PageableDefault(
                    sort = {"id"},
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllClients(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getClientById(id));
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(
            @Validated @RequestBody ClientRequestDto clientDto) {
        ClientResponseDto savedClient = service.createClient(clientDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedClient.id())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable UUID id, @Validated @RequestBody ClientRequestDto dto) {
        return ResponseEntity.ok(service.updateClient(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        service.deleteClient(id);

        return ResponseEntity.noContent().build();
    }

}
