package com.brassbook.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AlbumResponse {
    private Long id;
    private String name;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private long recordCount;
}