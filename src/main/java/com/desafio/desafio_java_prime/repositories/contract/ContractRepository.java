package com.desafio.desafio_java_prime.repositories.contract;

import com.desafio.desafio_java_prime.models.contract.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    Page<Contract> findByClientId(UUID clientId, Pageable pageable);

}
