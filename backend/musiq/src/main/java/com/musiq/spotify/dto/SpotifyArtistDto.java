package com.musiq.spotify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyArtistDto(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("images") List<ImageDto> images
) {}
