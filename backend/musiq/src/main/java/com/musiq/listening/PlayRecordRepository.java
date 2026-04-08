package com.musiq.listening;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.musiq.track.Artist;
import com.musiq.track.Song;
import com.musiq.user.User;

public interface PlayRecordRepository extends JpaRepository<PlayRecord, Long> {

    boolean existsByUserAndSongAndPlayedAt(User user, Song song, Instant playedAt);

    List<PlayRecord> findByUserOrderByPlayedAtDesc(User user, Pageable pageable);

    @Query("SELECT pr.song FROM PlayRecord pr WHERE pr.user = :user AND pr.playedAt > :since GROUP BY pr.song ORDER BY COUNT(pr) DESC")
    List<Song> findTopSongsByUser(@Param("user") User user, @Param("since") Instant since, Pageable pageable);

    @Query("SELECT a FROM PlayRecord pr JOIN pr.song.artists a WHERE pr.user = :user AND pr.playedAt > :since GROUP BY a ORDER BY COUNT(pr) DESC")
    List<Artist> findTopArtistsByUser(@Param("user") User user, @Param("since") Instant since, Pageable pageable);

    @Query("SELECT pr.song, COUNT(pr) FROM PlayRecord pr WHERE pr.user = :user AND pr.playedAt > :from AND pr.playedAt <= :to GROUP BY pr.song ORDER BY COUNT(pr) DESC")
    List<Object[]> findTopSongsWithCountByUserBetween(@Param("user") User user, @Param("from") Instant from, @Param("to") Instant to, Pageable pageable);

    @Query("SELECT a FROM PlayRecord pr JOIN pr.song.artists a WHERE pr.user = :user AND pr.playedAt > :from AND pr.playedAt <= :to GROUP BY a ORDER BY COUNT(pr) DESC")
    List<Artist> findTopArtistsByUserBetween(@Param("user") User user, @Param("from") Instant from, @Param("to") Instant to, Pageable pageable);

}
