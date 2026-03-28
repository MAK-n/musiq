package com.musiq.spotify.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyRecentlyPlayedDto(
    @JsonProperty("items") List<PlayHistoryDto> items
) {
    public record PlayHistoryDto(
        @JsonProperty("track") SpotifyTrackDto track,
        @JsonProperty("played_at") Instant playedAt
    ) {}
}
