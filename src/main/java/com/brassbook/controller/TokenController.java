package com.brassbook.controller;

import com.brassbook.dto.response.TokenResponse;
import com.brassbook.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private static final Logger log = LoggerFactory.getLogger(TokenController.class);

    @GetMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request) {
        log.info("Called refreshToken");
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        String refreshToken = authHeader.substring(7);
        return ResponseEntity.status(HttpStatus.OK)
                .body(tokenService.refreshToken(refreshToken));
    }
}
