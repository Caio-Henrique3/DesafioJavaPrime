package com.desafio.desafio_java_prime.controllers.contracts.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ContractRequestDto {

    @NotNull(message = "Client ID is required")
    private UUID clientId;

    @NotBlank(message = "Contract number is required")
    private String contractNumber;

    @NotNull(message = "Signature date is required")
    private LocalDate signatureDate;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

}
