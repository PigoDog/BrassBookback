package com.brassbook.service;

import com.brassbook.dto.request.RateRecordRequest;
import com.brassbook.dto.response.RecordResponse;
import com.brassbook.entity.FavoriteRecord;
import com.brassbook.entity.Record;
import com.brassbook.entity.RecordRating;
import com.brassbook.entity.User;
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
public class RecordService {

    private final RecordRepository recordRepository;
    private final FavoriteRecordRepository favoriteRecordRepository;
    private final RecordRatingRepository recordRatingRepository;

    // ==================== МОИ ЗАПИСИ ====================

    @Transactional(readOnly = true)
    public Page<RecordResponse> getMyRecords(Long userId, int page, String sortBy) {
        Pageable pageable = PageRequest.of(page, 20, getRecordSort(sortBy));

        return recordRepository.findUserRecords(userId, pageable)
                .map(record -> toRecordResponse(record, userId));
    }

    @Transactional
    public void deleteMyRecord(Long userId, Long recordId) {
        checkUserAccessToRecord(userId, recordId);

        Record record = findRecordOrThrow(recordId);
        record.setIsDeleted(true);

        recordRepository.save(record);
    }

    // ==================== ИЗБРАННОЕ ====================

    @Transactional(readOnly = true)
    public Page<RecordResponse> getFavorites(Long userId, int page, String sortBy) {
        Pageable pageable = PageRequest.of(page, 20, getFavoriteSort(sortBy));

        return favoriteRecordRepository.findByUserId(userId, pageable)
                .map(favorite -> toRecordResponse(favorite.getRecord(), userId));
    }

    @Transactional
    public void addToFavorites(Long userId, Long recordId) {
        checkUserAccessToRecord(userId, recordId);

        Record record = findRecordOrThrow(recordId);

        if (favoriteRecordRepository.existsByUserIdAndRecordId(userId, recordId)) {
            return;
        }

        User user = User.builder()
                .id(userId)
                .build();

        FavoriteRecord favoriteRecord = FavoriteRecord.builder()
                .user(user)
                .record(record)
                .build();

        favoriteRecordRepository.save(favoriteRecord);
    }

    @Transactional
    public void removeFromFavorites(Long userId, Long recordId) {
        favoriteRecordRepository.deleteByUserIdAndRecordId(userId, recordId);
    }

    // ==================== ОЦЕНКИ ====================

    @Transactional
    public RecordResponse rateRecord(Long userId, Long recordId, RateRecordRequest request) {
        checkUserAccessToRecord(userId, recordId);

        Record record = findRecordOrThrow(recordId);

        RecordRating rating = recordRatingRepository
                .findByUserIdAndRecordId(userId, recordId)
                .orElseGet(() -> {
                    User user = User.builder()
                            .id(userId)
                            .build();

                    return RecordRating.builder()
                            .user(user)
                            .record(record)
                            .build();
                });

        rating.setRating(request.getRating());
        recordRatingRepository.save(rating);

        return toRecordResponse(record, userId);
    }

    // ==================== ПОИСК ====================

    @Transactional(readOnly = true)
    public Page<RecordResponse> searchRecords(Long userId, String query, int page, String sortBy) {
        Pageable pageable = PageRequest.of(page, 20, getRecordSort(sortBy));

        return recordRepository.searchUserRecords(userId, query, pageable)
                .map(record -> toRecordResponse(record, userId));
    }

    // ==================== СКАЧИВАНИЕ ====================

    @Transactional(readOnly = true)
    public String getDownloadUrl(Long userId, Long recordId) {
        checkUserAccessToRecord(userId, recordId);

        Record record = findRecordOrThrow(recordId);

        if (record.getFileUrl() == null || record.getFileUrl().isBlank()) {
            throw new RuntimeException("У записи нет файла для скачивания");
        }

        return record.getFileUrl();
    }

    // ==================== ВСПОМОГАТЕЛЬНОЕ ====================

    private Record findRecordOrThrow(Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        if (Boolean.TRUE.equals(record.getIsDeleted())) {
            throw new RuntimeException("Запись удалена");
        }

        return record;
    }

    private void checkUserAccessToRecord(Long userId, Long recordId) {
        if (!recordRepository.existsUserAccessToRecord(userId, recordId)) {
            throw new RuntimeException("Запись не принадлежит пользователю");
        }
    }

    private Sort getRecordSort(String sortBy) {
        return switch (sortBy) {
            case "name" -> Sort.by("title").ascending();
            default -> Sort.by("createdAt").descending();
        };
    }

    private Sort getFavoriteSort(String sortBy) {
        return switch (sortBy) {
            case "name" -> Sort.by("record.title").ascending();
            default -> Sort.by("record.createdAt").descending();
        };
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