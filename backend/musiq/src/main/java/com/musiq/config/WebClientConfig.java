package com.musiq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.musiq.spotify.config.SpotifyProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final SpotifyProperties spotifyProperties;

    @Bean
    public WebClient spotifyApiClient(){
        return WebClient.builder()
        .baseUrl("https://api.spotify.com/v1")
        .build();
    }
    
    @Bean
    public WebClient spotifyTokenClient(){
        return WebClient.builder()
        .baseUrl(spotifyProperties.getTokenUri())
        .build();
    }
}
