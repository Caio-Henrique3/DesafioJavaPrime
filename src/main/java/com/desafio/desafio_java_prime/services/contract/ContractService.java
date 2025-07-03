package com.desafio.desafio_java_prime.services.contract;

import com.desafio.desafio_java_prime.controllers.contracts.dto.ContractRequestDto;
import com.desafio.desafio_java_prime.controllers.contracts.dto.ContractResponseDto;
import com.desafio.desafio_java_prime.exceptions.BusinessException;
import com.desafio.desafio_java_prime.exceptions.NotFoundException;
import com.desafio.desafio_java_prime.models.client.Client;
import com.desafio.desafio_java_prime.models.contract.Contract;
import com.desafio.desafio_java_prime.repositories.contract.ContractRepository;
import com.desafio.desafio_java_prime.services.client.ClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository repository;

    private final ClientService clientService;

    public List<ContractResponseDto> getAllContracts() {
        return repository.findAll().stream()
                .map(ContractResponseDto::fromEntity)
                .toList();
    }

    public List<ContractResponseDto> getContractByClientId(UUID clientId) {
        return repository.findByClientId(clientId).stream()
                .map(ContractResponseDto::fromEntity)
                .toList();
    }

    public Contract getContract(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contract not found with id: " + id));
    }

    public ContractResponseDto getContractById(UUID id) {
        return ContractResponseDto.fromEntity(getContract(id));
    }

    @Transactional
    public ContractResponseDto createContract(ContractRequestDto dto) {
        Client client = clientService.getClient(dto.getClientId());

        Contract contract = Contract.builder()
                .client(client)
                .amount(dto.getAmount())
                .endDate(dto.getEndDate())
                .startDate(dto.getStartDate())
                .signatureDate(dto.getSignatureDate())
                .contractNumber(dto.getContractNumber())
                .build();

        return ContractResponseDto.fromEntity(repository.save(contract));
    }

    @Transactional
    public ContractResponseDto update(UUID id, ContractRequestDto dto) {
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BusinessException("Start date cannot be after end date.");
        }

        Contract contract = getContract(id);

        if (!contract.getClient().getId().equals(dto.getClientId())) {
            Client client = clientService.getClient(dto.getClientId());

            contract.setClient(client);
        }

        contract.setAmount(dto.getAmount());
        contract.setEndDate(dto.getEndDate());
        contract.setStartDate(dto.getStartDate());
        contract.setSignatureDate(dto.getSignatureDate());
        contract.setContractNumber(dto.getContractNumber());

        return ContractResponseDto.fromEntity(repository.save(contract));
    }

    @Transactional
    public void deleteContract(UUID id) {
        Contract contract = getContract(id);

        if (Objects.nonNull(contract.getFilePath())) {
            try {
                Files.deleteIfExists(Path.of(contract.getFilePath()));
            } catch (IOException e) {
                throw new RuntimeException("Error deleting file", e);
            }
        }

        repository.delete(contract);
    }

    @Transactional
    public void uploadFile(UUID contractId, MultipartFile file) {
        if (!Objects.equals(file.getContentType(), "application/pdf")) {
            throw new BusinessException("Only PDF files are allowed.");
        }

        Contract contract = getContract(contractId);

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/contracts");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            contract.setFilePath(filePath.toString());
            repository.save(contract);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

}
