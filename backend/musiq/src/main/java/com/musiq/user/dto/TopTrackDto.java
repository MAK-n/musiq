package com.musiq.user.dto;

import java.util.List;

public record TopTrackDto(
    String spotifyId,
    String name,
    String imageUrl,
    long durationMs,
    String albumName,
    List<ArtistDto> artists,
    long playCount
) {}
