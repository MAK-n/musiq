package com.musiq.spotify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyUserProfileDto(
    String id,
    @JsonProperty("display_name") String displayName,
    String email,
    List<ImageDto> images
) {}
