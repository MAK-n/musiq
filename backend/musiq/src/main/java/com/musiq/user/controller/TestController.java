package com.musiq.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musiq.user.User;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        //get user set by jwt authentication filter
        User user = (User) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "displayName", user.getDisplayName(),
            "email", user.getEmail(),
            "spotifyId", user.getSpotifyId()
        ));
    }
    
}
