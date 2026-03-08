package com.musiq.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> { 
    boolean existsBySpotifyId(String spotifyId);
    Optional<User> findBySpotifyId(String spotifyId);
    Optional<User> findById(Long id);
}
