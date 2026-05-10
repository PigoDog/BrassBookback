package com.brassbook.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;
    private String email;
    private String displayName;
    private String displaySurname;
    private String photoUrl;
    private String role;
    private LocalDateTime createdAt;
}