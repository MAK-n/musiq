package com.musiq.track;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository  extends JpaRepository<Album, Long>{
    boolean existsBySpotifyId(String spotifyId);
    Optional<Album> findBySpotifyId(String spotifyId);
    Optional<Album> findById(Long id);
}

