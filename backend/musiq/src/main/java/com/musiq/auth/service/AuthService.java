package com.musiq.auth.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.musiq.auth.dto.AuthResponseDto;
import com.musiq.spotify.dto.SpotifyTokenResponse;
import com.musiq.spotify.dto.SpotifyUserProfileDto;
import com.musiq.spotify.service.SpotifyService;
import com.musiq.sync.SpotifySyncService;
import com.musiq.user.User;
import com.musiq.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SpotifyService spotifyService;
    private final JwtService jwtService;
    private final SpotifySyncService spotifySyncService;

    public AuthResponseDto handleSpotifyCallback(String code) {
        // 1- Exchange code for tokens
        SpotifyTokenResponse tokenResponse = spotifyService.exchangeCodeForTokens(code);

        // 2- Get user profile
        SpotifyUserProfileDto userProfile = spotifyService.getCurrentUserProfile(tokenResponse.accessToken());
        
        // 3- Create or update user
        String spotifyId = userProfile.spotifyId();
        User user = userRepository
        .findBySpotifyId(spotifyId)
        .map(existingUser -> {
            existingUser.setDisplayName(userProfile.displayName());
            existingUser.setAvatarUrl(userProfile.images().isEmpty() ? null : userProfile.images().get(0).url());
            existingUser.setUpdatedAt(Instant.now());
            existingUser.setAccessToken(tokenResponse.accessToken());
            existingUser.setRefreshToken(tokenResponse.refreshToken());
            existingUser.setExpiresAt(Instant.now().plusSeconds(tokenResponse.expiresIn()));
            return existingUser;
        })
        .orElseGet(() -> {
            User newUser = new User();
            newUser.setSpotifyId(spotifyId);
            newUser.setEmail(userProfile.email());
            newUser.setDisplayName(userProfile.displayName());
            newUser.setAvatarUrl(userProfile.images().isEmpty() ? null : userProfile.images().get(0).url());
            newUser.setCreatedAt(Instant.now());
            newUser.setUpdatedAt(Instant.now());
            newUser.setAccessToken(tokenResponse.accessToken());
            newUser.setRefreshToken(tokenResponse.refreshToken());
            newUser.setExpiresAt(Instant.now().plusSeconds(tokenResponse.expiresIn()));
            return newUser;
        });
        userRepository.save(user);

        // 4 - Sync User Data
        spotifySyncService.syncRecentlyPlayed(user);

        // 5- Generate JWT token
        String jwt = jwtService.generateToken(user.getId());

        // 6- Return auth dto object response
        return new AuthResponseDto(
            jwt,
            user.getSpotifyId(),
            user.getDisplayName(),
            user.getAvatarUrl()
        );

    }
}
