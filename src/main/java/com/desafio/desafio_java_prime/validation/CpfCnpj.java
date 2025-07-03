package com.desafio.desafio_java_prime.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CpfCnpjValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfCnpj {

    String message() default "Invalid CPF or CNPJ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
