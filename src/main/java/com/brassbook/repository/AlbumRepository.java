package com.brassbook.repository;

import com.brassbook.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Page<Album> findByUserId(Long userId, Pageable pageable);
    long countByUserId(Long userId);
}