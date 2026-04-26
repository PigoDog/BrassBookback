package com.brassbook.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RecordResponse {
    private Long id;
    private String title;
    private Integer duration;
    private String fileUrl;
    private Double averageRating;
    private LocalDateTime createdAt;
    private boolean isFavorite;
}