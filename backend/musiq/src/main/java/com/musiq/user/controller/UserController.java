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
    
    private Instant[] resolveBounds(String range, String from, String to) {
        if (from != null) {
            Instant f = Instant.parse(from);
            Instant t = to != null ? Instant.parse(to) : Instant.now();
            return new Instant[] { f, t };
        }
        Instant now = Instant.now();
        Instant since = switch (range != null ? range : "day") {
            case "day" -> now.minus(1, ChronoUnit.DAYS);
            case "week" -> now.minus(7, ChronoUnit.DAYS);
            case "month" -> now.minus(30, ChronoUnit.DAYS);
            case "year" -> now.minus(365, ChronoUnit.DAYS);
            default -> now.minus(180, ChronoUnit.DAYS);
        };
        return new Instant[] { since, now };
    }

    @GetMapping("/top-tracks")
    public ResponseEntity<List<TopTrackDto>> getTopTracks(
        @RequestParam(required = false) String range,
        @RequestParam(required = false) String to,
        @RequestParam(required = false) String from
    ) {
        Instant[] bounds = resolveBounds(range, from, to);
        List<Object[]> rows = playRecordRepository.findTopSongsWithCountByUserBetween(
            currentUser(), bounds[0], bounds[1], PageRequest.of(0, 50)
        );
        return ResponseEntity.ok(rows.stream()
            .map(row -> toTrackDto((Song) row[0], (Long) row[1]))
            .toList());
    }

    @GetMapping("/top-artists")
    public ResponseEntity<List<TopArtistDto>> getTopArtists(
        @RequestParam(required = false) String range,
        @RequestParam(required = false) String to,
        @RequestParam(required = false) String from
    ) {
        Instant[] bounds = resolveBounds(range, from, to);
        List<Artist> artists = playRecordRepository.findTopArtistsByUserBetween(
            currentUser(), bounds[0], bounds[1], PageRequest.of(0, 50)
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
            .map(r -> new RecentlyPlayedDto(toTrackDto(r.getSong(), 0), r.getPlayedAt()))
            .toList());
    }

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private TopTrackDto toTrackDto(Song song, long playCount) {
        List<ArtistDto> artists = song.getArtists().stream()
            .map(a -> new ArtistDto(a.getSpotifyId(), a.getName(), a.getImageUrl()))
            .toList();
        return new TopTrackDto(
            song.getSpotifyId(),
            song.getName(),
            song.getImageUrl(),
            song.getDurationMs(),
            song.getAlbum().getName(),
            artists,
            playCount
        );
    }
}
