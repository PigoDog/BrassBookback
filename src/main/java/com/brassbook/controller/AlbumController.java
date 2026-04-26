package com.brassbook.controller;

import com.brassbook.dto.request.CreateAlbumRequest;
import com.brassbook.dto.response.AlbumResponse;
import com.brassbook.dto.response.RecordResponse;
import com.brassbook.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponse>> getAlbums(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(albumService.getAlbums(userId, page, sortBy));
    }

    @PostMapping
    public ResponseEntity<AlbumResponse> createAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAlbumRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(albumService.createAlbum(userId, request));
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<AlbumResponse> renameAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CreateAlbumRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(albumService.renameAlbum(id, userId, request.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = getUserId(userDetails);
        albumService.deleteAlbum(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/records")
    public ResponseEntity<Page<RecordResponse>> getRecords(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(albumService.getRecordsInAlbum(id, userId, page, sortBy));
    }

    @PostMapping("/{albumId}/records/{recordId}")
    public ResponseEntity<Void> addRecordToAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long albumId,
            @PathVariable Long recordId) {
        Long userId = getUserId(userDetails);
        albumService.addRecordToAlbum(albumId, recordId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/records/{recordId}/move")
    public ResponseEntity<Void> moveRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long recordId,
            @RequestParam Long from,
            @RequestParam Long to) {
        Long userId = getUserId(userDetails);
        albumService.moveRecord(recordId, from, to, userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserId(UserDetails userDetails) {
        return Long.valueOf(userDetails.getUsername());
    }
}