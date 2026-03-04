package com.musiq.auth.service;

public class JwtService {

    public String generateToken(Long id) {
        return "mock-jwt-token" + id;
    }

}
