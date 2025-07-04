package com.desafio.desafio_java_prime.models.contract;

import com.desafio.desafio_java_prime.models.client.Client;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contracts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contract {

    @Id
    @EqualsAndHashCode.Include
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "signature_date")
    private LocalDate signatureDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @PrePersist
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

}
