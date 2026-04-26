package com.brassbook.repository;

import com.brassbook.entity.FavoriteRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRecordRepository extends JpaRepository<FavoriteRecord, Long> {

    @EntityGraph(attributePaths = "record")
    Page<FavoriteRecord> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndRecordId(Long userId, Long recordId);

    void deleteByUserIdAndRecordId(Long userId, Long recordId);
}