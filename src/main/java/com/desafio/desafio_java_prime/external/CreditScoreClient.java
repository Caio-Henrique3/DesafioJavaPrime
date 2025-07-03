package com.desafio.desafio_java_prime.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;


@Component
@RequiredArgsConstructor
public class CreditScoreClient {

    private final WebClient webClient;

    @Value("${external.credit-score.base-url}")
    private String baseUrl;

    public CreditScoreResponse getScore(String document) {
        return webClient.get()
                .uri(this.baseUrl + "/score/{document}", document)
                .retrieve()
                .bodyToMono(CreditScoreResponse.class)
                .block();
    }

}
