package com.musiq.auth.dto;

public record AuthResponseDto(
    String token,
    String spotifyId,
    String displayName,
    String avatarUrl
) {}
