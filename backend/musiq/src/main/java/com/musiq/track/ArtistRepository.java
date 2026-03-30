package com.musiq.track;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long>{
    boolean existsBySpotifyId(String spotifyId);
    Optional<Artist> findBySpotifyId(String spotifyId);
    Optional<Artist> findById(Long id);
}
