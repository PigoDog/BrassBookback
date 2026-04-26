package com.brassbook.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "record_ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "record_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RecordRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @Column(nullable = false)
    private Integer rating; // 1-5
}