package com.desafio.desafio_java_prime.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class CpfCnpjValidator implements ConstraintValidator<CpfCnpj, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        String document = value.replaceAll("\\D", "");

        if (document.length() == 11) {
            return isValidCpf(document);
        } else if (document.length() == 14) {
            return isValidCnpj(document);
        }

        return false;
    }

    private boolean isValidCpf(String cpf) {
        if (cpf.chars().distinct().count() == 1) return false;

        try {
            int d1 = 0, d2 = 0;
            for (int i = 0; i < 9; i++) {
                int digito = cpf.charAt(i) - '0';
                d1 += digito * (10 - i);
                d2 += digito * (11 - i);
            }

            int resto1 = (d1 * 10) % 11;
            resto1 = (resto1 == 10) ? 0 : resto1;

            int resto2 = ((d2 + resto1 * 2) * 10) % 11;
            resto2 = (resto2 == 10) ? 0 : resto2;

            return resto1 == (cpf.charAt(9) - '0') && resto2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidCnpj(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) return false;

        try {
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            int soma1 = 0, soma2 = 0;
            for (int i = 0; i < 12; i++) {
                int digito = cnpj.charAt(i) - '0';
                soma1 += digito * pesos1[i];
                soma2 += digito * pesos2[i];
            }

            int resto1 = soma1 % 11;
            int dig1 = (resto1 < 2) ? 0 : 11 - resto1;

            soma2 += dig1 * pesos2[12];
            int resto2 = soma2 % 11;
            int dig2 = (resto2 < 2) ? 0 : 11 - resto2;

            return dig1 == (cnpj.charAt(12) - '0') && dig2 == (cnpj.charAt(13) - '0');
        } catch (Exception e) {
            return false;
        }
    }

}
