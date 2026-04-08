package com.musiq.spotify.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import com.musiq.spotify.config.SpotifyProperties;
import com.musiq.spotify.dto.SpotifyArtistDto;
import com.musiq.spotify.dto.SpotifyArtistsBatchDto;
import com.musiq.spotify.dto.SpotifyRecentlyPlayedDto;
import com.musiq.spotify.dto.SpotifyTokenResponse;
import com.musiq.spotify.dto.SpotifyTopItemsDto;
import com.musiq.spotify.dto.SpotifyTrackDto;
import com.musiq.spotify.dto.SpotifyUserProfileDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpotifyService {
    private final SpotifyProperties spotifyProperties;

    @Qualifier("spotifyApiClient")
    private final WebClient spotifyApiClient;
    
    @Qualifier("spotifyTokenClient")
    private final WebClient spotifyTokenClient;

    
    public SpotifyTokenResponse exchangeCodeForTokens(String code){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", spotifyProperties.getRedirectUri());
        formData.add("client_id", spotifyProperties.getClientId());
        formData.add("client_secret", spotifyProperties.getClientSecret());
        

        return spotifyTokenClient
        .post()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(formData)
        .retrieve()
        .bodyToMono(SpotifyTokenResponse.class)
        .block();
    }

    public SpotifyTokenResponse refreshAccessToken(String refreshToken){
        MultiValueMap<String, String> formdata = new LinkedMultiValueMap<String, String>();
        formdata.add("grant_type", "refresh_token");
        formdata.add("refresh_token", refreshToken);
        formdata.add("client_id", spotifyProperties.getClientId());
        formdata.add("client_secret", spotifyProperties.getClientSecret());
    
        return spotifyTokenClient
        .post()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .bodyValue(formdata)
        .retrieve()
        .bodyToMono(SpotifyTokenResponse.class)
        .block();
    }
    
    public SpotifyUserProfileDto getCurrentUserProfile(String accessToken){
        return spotifyApiClient
        .get()
        .uri("/me")
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(SpotifyUserProfileDto.class)
        .block();
    }

    public SpotifyTopItemsDto<SpotifyTrackDto> getTopTracks(String accessToken, String timeRange){
        return spotifyApiClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/me/top/tracks")
            .queryParam("time_range", timeRange)
            .queryParam("limit", "50")
            .build()
        )
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<SpotifyTopItemsDto<SpotifyTrackDto>>() {})
        .block();
    }

    public SpotifyTopItemsDto<SpotifyArtistDto> getTopArtists(String accessToken, String timeRange){
        return spotifyApiClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/me/top/artists")
            .queryParam("time_range", timeRange)
            .queryParam("limit", "50")
            .build()
        )
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<SpotifyTopItemsDto<SpotifyArtistDto>>() {})
        .block();
    }

    public List<SpotifyArtistDto> getArtistsByIds(String accessToken, List<String> ids) {
        String idsParam = String.join(",", ids);
        SpotifyArtistsBatchDto response = spotifyApiClient
            .get()
            .uri("/artists?ids=" + idsParam)
            .headers(headers -> headers.setBearerAuth(accessToken))
            .retrieve()
            .bodyToMono(SpotifyArtistsBatchDto.class)
            .block();
        return (response != null && response.artists() != null) ? response.artists() : List.of();
    }

    public SpotifyRecentlyPlayedDto getRecentlyPlayed(String accessToken){
        return spotifyApiClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/me/player/recently-played")
            .queryParam("limit", "50")
            .build()
        )
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<SpotifyRecentlyPlayedDto>() {})
        .block();
    }
}
