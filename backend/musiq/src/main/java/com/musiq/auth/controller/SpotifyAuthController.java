package com.musiq.auth.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    
    @Value("${app.frontend-url}")
    private String frontendUrl;

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
            .encode()
            .toUriString();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam String code,
        @RequestParam(required = false) String error
    ) {
        if(error != null){
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, null, null, null));
        }
        AuthResponseDto response = authService.handleSpotifyCallback(code);
        return ResponseEntity
        .status(302)
        .location(URI.create(frontendUrl + "/callback?jwt=" + response.token()))
        .build();
    }
}
