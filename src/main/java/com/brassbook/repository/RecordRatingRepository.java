package com.brassbook.repository;

import com.brassbook.entity.RecordRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecordRatingRepository extends JpaRepository<RecordRating, Long> {
    Optional<RecordRating> findByUserIdAndRecordId(Long userId, Long recordId);

    @Query("SELECT AVG(r.rating) FROM RecordRating r WHERE r.record.id = :recordId")
    Double getAverageRatingByRecordId(@Param("recordId") Long recordId);
}