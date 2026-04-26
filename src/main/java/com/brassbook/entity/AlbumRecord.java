package com.brassbook.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "album_records")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AlbumRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @PrePersist
    void prePersist() {
        this.addedAt = LocalDateTime.now();
    }
}