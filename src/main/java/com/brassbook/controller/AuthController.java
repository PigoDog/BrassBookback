package com.brassbook.controller;

import com.brassbook.dto.request.LoginRequest;
import com.brassbook.dto.response.TokenResponse;
import com.brassbook.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/init")
    public ResponseEntity<TokenResponse> initUser() {
        log.info("Called initUser");
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.initUser());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        log.info("Called login");
        return ResponseEntity.status(HttpStatus.OK)
                .body(authService.login(loginRequest));
    }
}
