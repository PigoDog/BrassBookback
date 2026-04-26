package com.brassbook.repository;

import com.brassbook.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query("""
        SELECT DISTINCT r FROM Record r
        JOIN AlbumRecord ar ON ar.record = r
        JOIN Album a ON a = ar.album
        WHERE a.user.id = :userId
          AND r.isDeleted = false
        """)
    Page<Record> findUserRecords(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT DISTINCT r FROM Record r
        LEFT JOIN AlbumRecord ar ON ar.record = r
        LEFT JOIN Album a ON a = ar.album
        WHERE a.user.id = :userId
          AND r.isDeleted = false
          AND (
              LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))
          )
        """)
    Page<Record> searchUserRecords(@Param("userId") Long userId,
                                   @Param("query") String query,
                                   Pageable pageable);

    @Query("""
        SELECT CASE WHEN COUNT(ar) > 0 THEN true ELSE false END
        FROM AlbumRecord ar
        WHERE ar.record.id = :recordId
          AND ar.album.user.id = :userId
        """)
    boolean existsUserAccessToRecord(@Param("userId") Long userId,
                                     @Param("recordId") Long recordId);
}