package com.musiq.spotify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyArtistsBatchDto(
    @JsonProperty("artists") List<SpotifyArtistDto> artists
) {}
