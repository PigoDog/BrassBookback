package com.brassbook.controller;

import com.brassbook.dto.request.CodeRequest;
import com.brassbook.dto.request.PasswordRequest;
import com.brassbook.dto.request.RegistrationRequest;
import com.brassbook.dto.response.RegistrationResponse;
import com.brassbook.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> createUser(
            @RequestBody @Valid RegistrationRequest userToCreate
    ) {
        log.info("Called createUser");
        return ResponseEntity.status(HttpStatus.OK)
                .body(registrationService.createUser(userToCreate));
    }

    @PostMapping("/sendCode")
    public ResponseEntity<Void> sendCode(
            @RequestBody @Valid CodeRequest codeRequest
    ) {
        log.info("Called sendCode");
        registrationService.sendCode(codeRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/refreshCode")
    public ResponseEntity<Void> refreshCode(
            @RequestBody @Valid CodeRequest codeRequest
    ) {
        log.info("Called refreshCode");
        registrationService.refreshCode(codeRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/registration")
    public ResponseEntity<Void> updatePassword(
            @RequestBody @Valid PasswordRequest passwordRequest
    ) {
        log.info("Called updatePassword");
        registrationService.updatePassword(passwordRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
