package com.desafio.desafio_java_prime.controllers.clients.dto;

import com.desafio.desafio_java_prime.validation.CpfCnpj;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @CpfCnpj
    private String document;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

}
