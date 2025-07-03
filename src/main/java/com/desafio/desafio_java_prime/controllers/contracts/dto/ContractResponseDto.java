package com.desafio.desafio_java_prime.controllers.contracts.dto;

import com.desafio.desafio_java_prime.models.contract.Contract;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ContractResponseDto(
        UUID id,
        UUID clientId,
        String filePath,
        String contractNumber,
        LocalDate signatureDate,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal amount
) {
    public static ContractResponseDto fromEntity(Contract contract) {
        return ContractResponseDto.builder()
                .id(contract.getId())
                .clientId(contract.getClient().getId())
                .contractNumber(contract.getContractNumber())
                .signatureDate(contract.getSignatureDate())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .amount(contract.getAmount())
                .filePath(contract.getFilePath())
                .build();
    }
}