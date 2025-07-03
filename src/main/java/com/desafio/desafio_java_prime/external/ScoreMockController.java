package com.desafio.desafio_java_prime.external;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/score")
public class ScoreMockController {

    @GetMapping("/{document}")
    public Map<String, Object> getScore(@PathVariable String document) {
        int score = new Random().nextInt(1001);
        return Map.of(
                "document", document,
                "score", score
        );
    }

}
