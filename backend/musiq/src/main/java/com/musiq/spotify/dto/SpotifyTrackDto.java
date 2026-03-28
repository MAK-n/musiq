package com.musiq.spotify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTrackDto (
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("album") SpotifyAlbumDto album,
    @JsonProperty("artists") List<SpotifyArtistDto> artists,
    @JsonProperty("duration_ms") Long durationMs,
    @JsonProperty("preview_url") String previewUrl,
    @JsonProperty("image_url") String imageUrl,
    @JsonProperty("explicit") Boolean explicit
){}
