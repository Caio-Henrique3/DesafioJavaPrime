package com.desafio.desafio_java_prime.services.contract;

import com.desafio.desafio_java_prime.controllers.contracts.dto.ContractRequestDto;
import com.desafio.desafio_java_prime.controllers.contracts.dto.ContractResponseDto;
import com.desafio.desafio_java_prime.exceptions.BusinessException;
import com.desafio.desafio_java_prime.exceptions.NotFoundException;
import com.desafio.desafio_java_prime.external.CreditScoreClient;
import com.desafio.desafio_java_prime.external.CreditScoreResponse;
import com.desafio.desafio_java_prime.models.client.Client;
import com.desafio.desafio_java_prime.models.contract.Contract;
import com.desafio.desafio_java_prime.repositories.contract.ContractRepository;
import com.desafio.desafio_java_prime.services.client.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @InjectMocks
    private ContractService contractService;

    @Mock
    private ContractRepository repository;

    @Mock
    private ClientService clientService;

    @Mock
    private CreditScoreClient creditScoreClient;

    @Test
    void shouldReturnAllContractsPaged() {
        // Arrange
        Pageable pageable = buildPageable();
        Contract contract = buildValidContract();
        List<Contract> contracts = List.of(contract);
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());

        when(repository.findAll(pageable)).thenReturn(contractPage);

        // Act
        Page<ContractResponseDto> result = contractService.getAllContracts(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        ContractResponseDto dto = result.getContent().getFirst();
        assertEquals(contract.getAmount(), dto.amount());
        assertEquals(contract.getEndDate(), dto.endDate());
        assertEquals(contract.getStartDate(), dto.startDate());
        assertEquals(contract.getSignatureDate(), dto.signatureDate());
        assertEquals(contract.getContractNumber(), dto.contractNumber());

        assertInstanceOf(ContractResponseDto.class, result.getContent().getFirst());

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnAllContractsThroughThePaginatedClientId() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        Pageable pageable = buildPageable();
        List<Contract> contracts = List.of(buildValidContract());
        Page<Contract> contractPage = new PageImpl<>(contracts, pageable, contracts.size());

        when(repository.findByClientId(eq(clientId), eq(pageable))).thenReturn(contractPage);

        // Act
        Page<ContractResponseDto> result =
                contractService.getContractByClientId(clientId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        ContractResponseDto dto = result.getContent().getFirst();

        assertInstanceOf(ContractResponseDto.class, dto);

        verify(repository, times(1)).findByClientId(any(UUID.class), eq(pageable));
    }

    @Test
    void shouldReturnContractById() {
        // Arrange
        UUID contractId = UUID.randomUUID();
        Contract contract = buildValidContract();

        when(repository.findById(contractId)).thenReturn(Optional.of(contract));

        // Act
        ContractResponseDto result = contractService.getContractById(contractId);

        // Assert
        assertNotNull(result);
        assertEquals(contract.getId(), result.id());
        assertEquals(contract.getContractNumber(), result.contractNumber());

        assertInstanceOf(ContractResponseDto.class, result);

        verify(repository, times(1)).findById(contractId);
    }

    @Test
    void shouldThrowWhenContractNotFound() {
        // Arrange
        UUID contractId = UUID.randomUUID();

        when(repository.findById(contractId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> contractService.getContract(contractId)
        );

        assertEquals("Contract not found with id: " + contractId, exception.getMessage());

        verify(repository, times(1)).findById(contractId);
    }

    @Test
    void shouldCreateContract() {
        // Arrange
        Contract contract = buildValidContract();
        contract.setId(UUID.randomUUID());
        contract.getClient().setId(UUID.randomUUID());

        ContractRequestDto contractRequestDto = buildValidContractRequestDto();
        when(clientService.getClient(contractRequestDto.getClientId())).thenReturn(contract.getClient());
        when(repository.save(any(Contract.class))).thenReturn(contract);

        CreditScoreResponse mockScore =
                new CreditScoreResponse(contract.getClient().getDocument(), 700);
        when(creditScoreClient.getScore(contract.getClient().getDocument())).thenReturn(mockScore);

        // Act
        ContractResponseDto result = contractService.createContract(contractRequestDto);

        // Assert
        assertNotNull(result);

        assertInstanceOf(ContractResponseDto.class, result);

        verify(repository, times(1)).save(any(Contract.class));
    }

    @Test
    void shouldThrowWhenStartDateIsAfterEndDate() {
        // Arrange
        ContractRequestDto contractRequestDto = ContractRequestDto.builder()
                .clientId(UUID.randomUUID())
                .contractNumber("ABC123")
                .amount(new BigDecimal("10000"))
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2023, 1, 1))
                .signatureDate(LocalDate.of(2023, 12, 1))
                .build();

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> contractService.createContract(contractRequestDto)
        );

        assertEquals("Start date cannot be after end date.", exception.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTheScoreIsNotValid() {
        // Arrange
        Contract contract = buildValidContract();
        contract.getClient().setId(UUID.randomUUID());

        ContractRequestDto contractRequestDto = buildValidContractRequestDto();
        when(clientService.getClient(contractRequestDto.getClientId())).thenReturn(contract.getClient());

        CreditScoreResponse mockScore =
                new CreditScoreResponse(contract.getClient().getDocument(), 150);
        when(creditScoreClient.getScore(contract.getClient().getDocument())).thenReturn(mockScore);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class, () -> contractService.createContract(contractRequestDto)
        );

        assertEquals("Client has insufficient credit score: " + mockScore.score(), exception.getMessage());

        verify(repository, never()).save(any());
    }

    private Pageable buildPageable() {
        return PageRequest.of(0, 10);
    }

    private Contract buildValidContract() {
        return Contract.builder()
                .id(UUID.randomUUID())
                .contractNumber("ABC123")
                .amount(new BigDecimal("10000"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .signatureDate(LocalDate.of(2023, 12, 1))
                .client(Client.builder()
                        .id(UUID.randomUUID())
                        .name("User Test")
                        .document("12345678900")
                        .build())
                .build();
    }

    private ContractRequestDto buildValidContractRequestDto() {
        return ContractRequestDto.builder()
                .clientId(UUID.randomUUID())
                .contractNumber("ABC123")
                .amount(new BigDecimal("10000"))
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .signatureDate(LocalDate.of(2023, 12, 1))
                .build();
    }

}