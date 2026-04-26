package com.brassbook.service;

import com.brassbook.dto.request.CreateAlbumRequest;
import com.brassbook.dto.response.AlbumResponse;
import com.brassbook.dto.response.RecordResponse;
import com.brassbook.entity.Album;
import com.brassbook.entity.AlbumRecord;
import com.brassbook.entity.Record;
import com.brassbook.entity.User;
import com.brassbook.repository.AlbumRecordRepository;
import com.brassbook.repository.AlbumRepository;
import com.brassbook.repository.FavoriteRecordRepository;
import com.brassbook.repository.RecordRatingRepository;
import com.brassbook.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumRecordRepository albumRecordRepository;
    private final RecordRepository recordRepository;
    private final FavoriteRecordRepository favoriteRecordRepository;
    private final RecordRatingRepository recordRatingRepository;


    @Transactional(readOnly = true)
    public Page<AlbumResponse> getAlbums(Long userId, int page, String sortBy) {
        Sort sort = switch (sortBy) {
            case "name" -> Sort.by("name").ascending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, 20, sort);

        return albumRepository.findByUserId(userId, pageable)
                .map(this::toAlbumResponse);
    }

    @Transactional
    public AlbumResponse createAlbum(Long userId, CreateAlbumRequest request) {
        User user = User.builder()
                .id(userId)
                .build();

        Album album = Album.builder()
                .name(request.getName())
                .user(user)
                .build();

        Album savedAlbum = albumRepository.save(album);

        return toAlbumResponse(savedAlbum);
    }

    @Transactional
    public AlbumResponse renameAlbum(Long albumId, Long userId, String newName) {
        Album album = findAlbumOrThrow(albumId, userId);
        album.setName(newName);

        return toAlbumResponse(albumRepository.save(album));
    }

    @Transactional
    public void deleteAlbum(Long albumId, Long userId) {
        Album album = findAlbumOrThrow(albumId, userId);
        albumRepository.delete(album);
    }


    @Transactional(readOnly = true)
    public Page<RecordResponse> getRecordsInAlbum(Long albumId, Long userId, int page, String sortBy) {
        findAlbumOrThrow(albumId, userId);

        Sort sort = switch (sortBy) {
            case "name" -> Sort.by("record.title").ascending();
            default -> Sort.by("addedAt").descending();
        };

        Pageable pageable = PageRequest.of(page, 20, sort);

        return albumRecordRepository.findByAlbumId(albumId, pageable)
                .map(albumRecord -> toRecordResponse(albumRecord.getRecord(), userId));
    }

    @Transactional
    public void addRecordToAlbum(Long albumId, Long recordId, Long userId) {
        Album album = findAlbumOrThrow(albumId, userId);

        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        if (Boolean.TRUE.equals(record.getIsDeleted())) {
            throw new RuntimeException("Запись удалена");
        }

        if (albumRecordRepository.existsByAlbumIdAndRecordId(albumId, recordId)) {
            throw new RuntimeException("Запись уже есть в альбоме");
        }

        AlbumRecord albumRecord = AlbumRecord.builder()
                .album(album)
                .record(record)
                .build();

        albumRecordRepository.save(albumRecord);
    }

    @Transactional
    public void moveRecord(Long recordId, Long fromAlbumId, Long toAlbumId, Long userId) {
        findAlbumOrThrow(fromAlbumId, userId);
        Album toAlbum = findAlbumOrThrow(toAlbumId, userId);

        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        if (!albumRecordRepository.existsByAlbumIdAndRecordId(fromAlbumId, recordId)) {
            throw new RuntimeException("Записи нет в исходном альбоме");
        }

        if (albumRecordRepository.existsByAlbumIdAndRecordId(toAlbumId, recordId)) {
            throw new RuntimeException("Запись уже есть в целевом альбоме");
        }

        albumRecordRepository.deleteByAlbumIdAndRecordId(fromAlbumId, recordId);

        AlbumRecord newAlbumRecord = AlbumRecord.builder()
                .album(toAlbum)
                .record(record)
                .build();

        albumRecordRepository.save(newAlbumRecord);
    }


    private Album findAlbumOrThrow(Long albumId, Long userId) {
        return albumRepository.findById(albumId)
                .filter(album -> album.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Альбом не найден"));
    }

    private AlbumResponse toAlbumResponse(Album album) {
        return AlbumResponse.builder()
                .id(album.getId())
                .name(album.getName())
                .avatarUrl(album.getAvatarUrl())
                .createdAt(album.getCreatedAt())
                .recordCount(albumRecordRepository.countByAlbumId(album.getId()))
                .build();
    }

    private RecordResponse toRecordResponse(Record record, Long userId) {
        Double averageRating = recordRatingRepository.getAverageRatingByRecordId(record.getId());

        boolean isFavorite = favoriteRecordRepository.existsByUserIdAndRecordId(
                userId,
                record.getId()
        );

        return RecordResponse.builder()
                .id(record.getId())
                .title(record.getTitle())
                .duration(record.getDuration())
                .fileUrl(record.getFileUrl())
                .averageRating(averageRating)
                .createdAt(record.getCreatedAt())
                .isFavorite(isFavorite)
                .build();
    }
}