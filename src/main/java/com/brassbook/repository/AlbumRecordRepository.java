package com.brassbook.repository;

import com.brassbook.entity.AlbumRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRecordRepository extends JpaRepository<AlbumRecord, Long> {

    @EntityGraph(attributePaths = "record")
    Page<AlbumRecord> findByAlbumId(Long albumId, Pageable pageable);

    long countByAlbumId(Long albumId);

    void deleteByAlbumIdAndRecordId(Long albumId, Long recordId);

    boolean existsByAlbumIdAndRecordId(Long albumId, Long recordId);
}