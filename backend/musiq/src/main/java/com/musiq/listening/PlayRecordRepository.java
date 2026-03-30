package com.musiq.listening;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musiq.track.Song;
import com.musiq.user.User;

public interface PlayRecordRepository extends JpaRepository<PlayRecord, Long> {
    List<PlayRecord> findByUserIdOrderByPlayedAtDesc(Long userId);
    List<PlayRecord> findBySong(String song);
    List<PlayRecord> findByPlayedAtBetween(Instant start, Instant end);
    List<PlayRecord> findByPlayedAtAfter(Instant start);
    List<PlayRecord> findByPlayedAtBefore(Instant end);
    List<PlayRecord> findByPlayedAtBetweenAndUser(Instant start, Instant end, User user);
    List<PlayRecord> findByPlayedAtAfterAndUser(Instant start, User user);
    List<PlayRecord> findByPlayedAtBeforeAndUser(Instant end, User user);
    boolean existsByUserAndSongAndPlayedAt(User user, Song song, Instant playedAt);
}
