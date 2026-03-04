package com.musiq.auth.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.musiq.auth.dto.AuthResponseDto;
import com.musiq.auth.service.AuthService;
import com.musiq.spotify.config.SpotifyProperties;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/auth/spotify")
@RequiredArgsConstructor
public class SpotifyAuthController {
    private final SpotifyProperties spotifyProperties;
    private final AuthService authService;
    
    @GetMapping("/login")
    public Map<String, String> login() {
        String uri = buildAuthorizationUri();
        return Map.of("redirect_uri", uri);
    }

    private String buildAuthorizationUri() {
        return UriComponentsBuilder
            .fromUriString(spotifyProperties.getAuthorizationUri())
            .queryParam("client_id", spotifyProperties.getClientId())
            .queryParam("response_type", "code")
            .queryParam("redirect_uri", spotifyProperties.getRedirectUri())
            .queryParam("scope", spotifyProperties.getScopes())
            .build()
            .toString();
    }

    @GetMapping("/callback")
    public AuthResponseDto callback(@RequestParam String code,
        @RequestParam(required = false) String error
    ) {
        if(error != null) {
            throw new RuntimeException("Error: " + error);
        }
        return authService.handleSpotifyCallback(code);
    }
}
