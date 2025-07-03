package com.desafio.desafio_java_prime.models.contract;

import com.desafio.desafio_java_prime.models.client.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contract {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "signature_date")
    private LocalDate signatureDate;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

}
