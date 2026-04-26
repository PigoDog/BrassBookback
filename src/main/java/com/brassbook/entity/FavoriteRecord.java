package com.brassbook.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "record_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FavoriteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;
}