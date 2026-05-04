package com.brassbook.service;

import com.brassbook.config.JwtUtils;
import com.brassbook.dto.request.LoginRequest;
import com.brassbook.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse initUser() {
        UserDetails anonymousUser = org.springframework.security.core.userdetails.User.builder()
                .username("anonymous_" + UUID.randomUUID())
                .password("")
                .roles("ANONYMOUS")
                .build();

        String accessToken = jwtUtils.generateToken(anonymousUser);
        String refreshToken = jwtUtils.generateRefreshToken(anonymousUser);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        return new TokenResponse(accessToken, refreshToken);
    }
}
