package com.desafio.desafio_java_prime.controllers.contracts;

import com.desafio.desafio_java_prime.controllers.contracts.dto.ContractRequestDto;
import com.desafio.desafio_java_prime.controllers.contracts.dto.ContractResponseDto;
import com.desafio.desafio_java_prime.exceptions.NotFoundException;
import com.desafio.desafio_java_prime.models.contract.Contract;
import com.desafio.desafio_java_prime.services.contract.ContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@Tag(name = "Contracts")
public class ContractController {

    private final ContractService service;

    @GetMapping
    public ResponseEntity<Page<ContractResponseDto>> getAllContracts(
            @PageableDefault(
                    sort = {"id"},
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllContracts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponseDto> getContractById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getContractById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<ContractResponseDto>> getContractsByClient(
            @PathVariable UUID clientId,
            @PageableDefault(
                    sort = {"id"},
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getContractByClientId(clientId, pageable));
    }

    @PostMapping
    public ResponseEntity<ContractResponseDto> createContract(@Valid @RequestBody ContractRequestDto requestDto) {
        ContractResponseDto savedContract = service.createContract(requestDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedContract.id())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractResponseDto> updateContract(@PathVariable UUID id,
                                                              @Valid @RequestBody ContractRequestDto requestDto) {
        return ResponseEntity.ok(service.updateContract(id, requestDto));
    }

    @PostMapping(path = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadContractFile(@PathVariable UUID id,
                                                   @RequestParam("file") MultipartFile file) {
        service.uploadFile(id, file);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID id) throws IOException {
        Contract contract = service.getContract(id);

        Path path = Path.of(contract.getFilePath());

        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new NotFoundException("File not found for contract id: " + id);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName())
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable UUID id) {
        service.deleteContract(id);

        return ResponseEntity.noContent().build();
    }

}
