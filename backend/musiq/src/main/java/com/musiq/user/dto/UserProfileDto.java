package com.musiq.user.dto;

public record UserProfileDto(
    String spotifyId,
    String displayName,
    String email,
    String avatarUrl
) {}
