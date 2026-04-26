package com.brassbook.controller;

import com.brassbook.dto.request.RateRecordRequest;
import com.brassbook.dto.response.RecordResponse;
import com.brassbook.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/my")
    public ResponseEntity<Page<RecordResponse>> getMyRecords(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(recordService.getMyRecords(userId, page, sortBy));
    }

    @GetMapping("/favorites")
    public ResponseEntity<Page<RecordResponse>> getFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(recordService.getFavorites(userId, page, sortBy));
    }

    @PostMapping("/{recordId}/favorite")
    public ResponseEntity<Void> addToFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long recordId) {

        Long userId = getUserId(userDetails);
        recordService.addToFavorites(userId, recordId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{recordId}/favorite")
    public ResponseEntity<Void> removeFromFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long recordId) {

        Long userId = getUserId(userDetails);
        recordService.removeFromFavorites(userId, recordId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{recordId}/rating")
    public ResponseEntity<RecordResponse> rateRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long recordId,
            @Valid @RequestBody RateRecordRequest request) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(recordService.rateRecord(userId, recordId, request));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RecordResponse>> searchRecords(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(recordService.searchRecords(userId, query, page, sortBy));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteMyRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long recordId) {

        Long userId = getUserId(userDetails);
        recordService.deleteMyRecord(userId, recordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{recordId}/download")
    public ResponseEntity<Void> downloadRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long recordId) {

        Long userId = getUserId(userDetails);
        String fileUrl = recordService.getDownloadUrl(userId, recordId);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(fileUrl))
                .build();
    }

    private Long getUserId(UserDetails userDetails) {
        return Long.valueOf(userDetails.getUsername());
    }
}