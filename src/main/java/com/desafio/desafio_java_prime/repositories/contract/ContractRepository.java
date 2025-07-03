package com.desafio.desafio_java_prime.repositories.contract;

import com.desafio.desafio_java_prime.models.contract.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    List<Contract> findByClientId(UUID clientId);

}
