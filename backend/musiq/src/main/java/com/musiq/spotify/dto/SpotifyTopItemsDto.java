package com.musiq.spotify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

//reusable wrapper for top tracks and artists
public record SpotifyTopItemsDto<T>(
    @JsonProperty("items") List<T> items
) {

}
