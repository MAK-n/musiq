package com.musiq.spotify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyUserProfileDto(
    @JsonProperty("id") String spotifyId,
    @JsonProperty("display_name") String displayName,
    @JsonProperty("email") String email,
    @JsonProperty("images") List<ImageDto> images
) {}
