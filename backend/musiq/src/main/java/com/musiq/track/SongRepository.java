package com.musiq.track;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository  extends JpaRepository<Song, Long>{
    boolean existsBySpotifyId(String spotifyId);
    Optional<Song> findBySpotifyId(String spotifyId);
    Optional<Song> findById(Long id);
}
