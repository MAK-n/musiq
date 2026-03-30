package com.musiq.sync;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.musiq.user.User;
import com.musiq.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpotifySyncScheduler {
    private final UserRepository userRepository;
    private final SpotifySyncService spotifySyncService;

    @Scheduled(cron = "0 0 * * * *")
    public void syncRecentlyPlayed() {
        List<User> users = userRepository.findAll();
        for(User user : users) {
            try{
                spotifySyncService.syncRecentlyPlayed(user);
                log.info("Synced recently played for user: {}", user.getSpotifyId());
            }
            catch(Exception e) {
                log.error("Error syncing recently played for user: {}", user.getSpotifyId(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void syncTopTracks() {
        List<User> users = userRepository.findAll();
        for(User user: users){
            try{
                spotifySyncService.syncTopTracks(user, "short_term");
                spotifySyncService.syncTopTracks(user, "medium_term");
                spotifySyncService.syncTopTracks(user, "long_term");
                log.info("Synced top tracks for user: {}", user.getSpotifyId());
            }
            catch(Exception e) {
                log.error("Error syncing top tracks for user: {}", user.getSpotifyId(), e);
            }
        }
    }
    
    @Scheduled(cron = "0 0 0 * * *")
    public void syncTopArtist() {
        List<User> users = userRepository.findAll();
        for(User user: users){
            try{
                spotifySyncService.syncTopArtists(user, "short_term");
                spotifySyncService.syncTopArtists(user, "medium_term");
                spotifySyncService.syncTopArtists(user, "long_term");
                log.info("Synced top Artist for user: {}", user.getSpotifyId());
            }
            catch(Exception e) {
                log.error("Error syncing top Artist for user: {}", user.getSpotifyId(), e);
            }
        }
    }
}
