package com.desafio.desafio_java_prime.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CreditScoreClient {

    private final WebClient webClient;

    @Autowired
    public CreditScoreClient(@Value("${external.credit-score.base-url}") String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public CreditScoreResponse getScore(String document) {
        return webClient.get()
                .uri("/scores/{document}", document)
                .retrieve()
                .bodyToMono(CreditScoreResponse.class)
                .block();
    }

}
