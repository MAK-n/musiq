package com.musiq.user.dto;

import java.time.Instant;

public record RecentlyPlayedDto(
    TopTrackDto track,
    Instant playedAt
) {}
