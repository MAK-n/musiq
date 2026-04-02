package com.musiq.user.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.musiq.listening.PlayRecord;
import com.musiq.listening.PlayRecordRepository;
import com.musiq.track.Artist;
import com.musiq.track.Song;
import com.musiq.user.User;
import com.musiq.user.dto.ArtistDto;
import com.musiq.user.dto.RecentlyPlayedDto;
import com.musiq.user.dto.TopArtistDto;
import com.musiq.user.dto.TopTrackDto;
import com.musiq.user.dto.UserProfileDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/me")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserController {

    private final PlayRecordRepository playRecordRepository;

    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile() {
        User user = currentUser();
        return ResponseEntity.ok(new UserProfileDto(
            user.getSpotifyId(),
            user.getDisplayName(),
            user.getEmail(),
            user.getAvatarUrl()
        ));
    }

    @GetMapping("/top-tracks")
    public ResponseEntity<List<TopTrackDto>> getTopTracks(
        @RequestParam(defaultValue = "medium_term") String range
    ) {
        List<Song> songs = playRecordRepository.findTopSongsByUser(
            currentUser(), rangeToInstant(range), PageRequest.of(0, 50)
        );
        return ResponseEntity.ok(songs.stream().map(this::toTrackDto).toList());
    }

    @GetMapping("/top-artists")
    public ResponseEntity<List<TopArtistDto>> getTopArtists(
        @RequestParam(defaultValue = "medium_term") String range
    ) {
        List<Artist> artists = playRecordRepository.findTopArtistsByUser(
            currentUser(), rangeToInstant(range), PageRequest.of(0, 50)
        );
        return ResponseEntity.ok(artists.stream()
            .map(a -> new TopArtistDto(a.getSpotifyId(), a.getName(), a.getImageUrl()))
            .toList());
    }

    @GetMapping("/recently-played")
    public ResponseEntity<List<RecentlyPlayedDto>> getRecentlyPlayed() {
        List<PlayRecord> records = playRecordRepository
            .findByUserOrderByPlayedAtDesc(currentUser(), PageRequest.of(0, 50));
        return ResponseEntity.ok(records.stream()
            .map(r -> new RecentlyPlayedDto(toTrackDto(r.getSong()), r.getPlayedAt()))
            .toList());
    }

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Instant rangeToInstant(String range) {
        return switch (range) {
            case "short_term" -> Instant.now().minus(28, ChronoUnit.DAYS);
            case "long_term"  -> Instant.now().minus(365, ChronoUnit.DAYS);
            default           -> Instant.now().minus(180, ChronoUnit.DAYS);
        };
    }

    private TopTrackDto toTrackDto(Song song) {
        List<ArtistDto> artists = song.getArtists().stream()
            .map(a -> new ArtistDto(a.getSpotifyId(), a.getName(), a.getImageUrl()))
            .toList();
        return new TopTrackDto(
            song.getSpotifyId(),
            song.getName(),
            song.getImageUrl(),
            song.getDurationMs(),
            song.getAlbum().getName(),
            artists
        );
    }
}
