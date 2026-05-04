package com.brassbook.service;

import com.brassbook.config.JwtUtils;
import com.brassbook.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    public TokenResponse refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtUtils.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String email = jwtUtils.getEmail(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String newAccessToken = jwtUtils.generateToken(userDetails);
        String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);
        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
