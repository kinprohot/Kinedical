package com.kinedical.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${spring.recommend-api.base-url:http://localhost:8000}")
    private String recommendApiBaseUrl;

    @Bean
    public WebClient recommendWebClient() {
        return WebClient.builder()
                .baseUrl(recommendApiBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
