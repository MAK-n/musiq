package com.musiq.sync;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.musiq.listening.PlayRecord;
import com.musiq.listening.PlayRecordRepository;
import com.musiq.spotify.dto.*;
import com.musiq.spotify.service.SpotifyService;
import com.musiq.track.*;
import com.musiq.user.User;
import com.musiq.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpotifySyncService {

    private final SpotifyService spotifyService;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final PlayRecordRepository playRecordRepository;

    // Refresh Token if required

    private String getValidAccessToken(User user) {
        if (Instant.now().isAfter(user.getExpiresAt())) {
            SpotifyTokenResponse refreshed = spotifyService.refreshAccessToken(user.getRefreshToken());
            user.setAccessToken(refreshed.accessToken());
            user.setExpiresAt(Instant.now().plusSeconds(refreshed.expiresIn()));
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        }
        return user.getAccessToken();
    }

    // Sync Methods

    @Transactional
    public void syncTopTracks(User user, String timeRange) {
        String token = getValidAccessToken(user);
        SpotifyTopItemsDto<SpotifyTrackDto> response = spotifyService.getTopTracks(token, timeRange);

        for (SpotifyTrackDto trackDto : response.items()) {
            Album album = syncAlbum(trackDto.album());
            List<Artist> artists = syncArtists(trackDto.artists());
            syncSong(trackDto, album, artists);
        }
    }

    @Transactional
    public void syncTopArtists(User user, String timeRange) {
        String token = getValidAccessToken(user);
        SpotifyTopItemsDto<SpotifyArtistDto> response = spotifyService.getTopArtists(token, timeRange);

        for (SpotifyArtistDto artistDto : response.items()) {
            syncArtists(List.of(artistDto));
        }
    }

    @Transactional
    public void syncRecentlyPlayed(User user) {
        String token = getValidAccessToken(user);
        SpotifyRecentlyPlayedDto response = spotifyService.getRecentlyPlayed(token);

        for (SpotifyRecentlyPlayedDto.PlayHistoryDto item : response.items()) {
            Album album = syncAlbum(item.track().album());
            List<Artist> artists = syncArtists(item.track().artists());
            Song song = syncSong(item.track(), album, artists);

            if (!playRecordRepository.existsByUserAndSongAndPlayedAt(user, song, item.playedAt())) {
                PlayRecord record = PlayRecord.builder()
                    .user(user)
                    .song(song)
                    .playedAt(item.playedAt())
                    .build();
                playRecordRepository.save(record);
            }
        }
    }

    // Helper Methods

    private Album syncAlbum(SpotifyAlbumDto albumDto) {
        return albumRepository.findBySpotifyId(albumDto.id())
            .map(existing -> {
                existing.setName(albumDto.name());
                existing.setImageUrl(albumDto.images() == null || albumDto.images().isEmpty()
                    ? null : albumDto.images().get(0).url());
                existing.setReleaseDate(albumDto.releaseDate());
                return albumRepository.save(existing);
            })
            .orElseGet(() -> albumRepository.save(Album.builder()
                .spotifyId(albumDto.id())
                .name(albumDto.name())
                .imageUrl(albumDto.images() == null || albumDto.images().isEmpty()
                    ? null : albumDto.images().get(0).url())
                .releaseDate(albumDto.releaseDate())
                .build()
            ));
    }

    private List<Artist> syncArtists(List<SpotifyArtistDto> artistDtos) {
        return artistDtos.stream()
            .map(this::syncArtist)
            .toList();
    }

    private Artist syncArtist(SpotifyArtistDto artistDto) {
        return artistRepository.findBySpotifyId(artistDto.id())
            .map(existing -> {
                existing.setName(artistDto.name());
                existing.setImageUrl(artistDto.images() == null || artistDto.images().isEmpty()
                ? null : artistDto.images().get(0).url());
                return artistRepository.save(existing);
            })
            .orElseGet(() -> artistRepository.save(Artist.builder()
                .spotifyId(artistDto.id())
                .name(artistDto.name())
                .imageUrl(artistDto.images() == null || artistDto.images().isEmpty()
                ? null : artistDto.images().get(0).url())
                .build()
            ));
    }

    private Song syncSong(SpotifyTrackDto trackDto, Album album, List<Artist> artists) {
        return songRepository.findBySpotifyId(trackDto.id())
            .map(existing -> {
                existing.setName(trackDto.name());
                existing.setDurationMs(trackDto.durationMs().intValue());
                existing.setPreviewUrl(trackDto.previewUrl());
                existing.setExplicit(trackDto.explicit());
                existing.setImageUrl(album.getImageUrl());
                existing.setAlbum(album);
                existing.setArtists(artists);
                return songRepository.save(existing);
            })
            .orElseGet(() -> songRepository.save(Song.builder()
                .spotifyId(trackDto.id())
                .name(trackDto.name())
                .durationMs(trackDto.durationMs().intValue())
                .previewUrl(trackDto.previewUrl())
                .explicit(trackDto.explicit())
                .imageUrl(album.getImageUrl())
                .album(album)
                .artists(artists)
                .build()
            ));
    }
}