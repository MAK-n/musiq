package com.musiq.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.musiq.auth.service.JwtService;
import com.musiq.user.User;
import com.musiq.user.UserRepository;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException, java.io.IOException {
        
        //1. get the auth header
        String authHeader = request.getHeader("Authorization");

        //2. if no token, skip and continue
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //3. extract the token
        String token = authHeader.substring(7);

        //4. validate token
        if(!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        //5. extract userId from token
        Long userId = jwtService.extractUserId(token);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        //6. set authentication in security context
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            user, // the user object
            null, // credentials - null since jwt is used
            user.getAuthorities() // authorities/roles - empty for now
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
