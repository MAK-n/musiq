package com.musiq.spotify.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.musiq.spotify.config.SpotifyProperties;
import com.musiq.spotify.dto.SpotifyTokenResponse;
import com.musiq.spotify.dto.SpotifyUserProfileDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpotifyService {
    private final SpotifyProperties spotifyProperties;
    private final WebClient webClient;

    public SpotifyService(SpotifyProperties spotifyProperties, WebClient.Builder webClientBuilder) {
        this.spotifyProperties = spotifyProperties;
        this.webClient = webClientBuilder
            .baseUrl("https://api.spotify.com/v1")
            .build();
    }
    
    public SpotifyTokenResponse exchangeCodeForTokens(String code){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", spotifyProperties.getRedirectUri());
        formData.add("client_id", spotifyProperties.getClientId());
        formData.add("client_secret", spotifyProperties.getClientSecret());
        

        return webClient
        .post()
        .uri(spotifyProperties.getTokenUri())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(formData)
        .retrieve()
        .bodyToMono(SpotifyTokenResponse.class)
        .block();
    }

    public SpotifyUserProfileDto getCurrentUserProfile(String accessToken){
        return webClient
        .get()
        .uri("/me")
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(SpotifyUserProfileDto.class)
        .block();
    }
}
