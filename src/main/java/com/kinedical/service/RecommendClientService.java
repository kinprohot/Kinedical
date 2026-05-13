package com.kinedical.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.kinedical.dto.RecommendRequest;
import com.kinedical.dto.RecommendResponse;

import reactor.core.publisher.Mono;

@Service
public class RecommendClientService {

    private final WebClient recommendWebClient;

    public RecommendClientService(WebClient recommendWebClient) {
        this.recommendWebClient = recommendWebClient;
    }

    public Mono<RecommendResponse> recommendAsync(RecommendRequest request) {
        return recommendWebClient.post()
                .uri("/recommend")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RecommendResponse.class)
                .onErrorMap(ex -> new RuntimeException("Recommend API request failed", ex));
    }
}
