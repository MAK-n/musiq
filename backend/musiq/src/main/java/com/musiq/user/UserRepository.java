package com.musiq.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);   
    boolean existsBySpotifyId(String spotifyId);
    Optional<User> findBySpotifyId(String spotifyId);
}
