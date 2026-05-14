package com.brassbook.service;

import com.brassbook.dto.request.ChangePasswordRequest;
import com.brassbook.dto.request.UpdateProfileRequest;
import com.brassbook.dto.response.ProfileResponse;
import com.brassbook.entity.User;
import com.brassbook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = findUserOrThrow(userId);
        return toProfileResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный текущий пароль");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUserOrThrow(userId);

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }

        if (request.getDisplaySurname() != null) {
            user.setDisplaySurname(request.getDisplaySurname());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        User savedUser = userRepository.save(user);
        return toProfileResponse(savedUser);
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = findUserOrThrow(userId);
        userRepository.delete(user);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    private ProfileResponse toProfileResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .displaySurname(user.getDisplaySurname())
                .photoUrl(user.getPhotoUrl())
                .role(user.getRole().name())
                .companyName(user.getCompanyName())
                .profession(user.getProfession())
                .inn(user.getInn())
                .createdAt(user.getCreatedAt())
                .build();
    }
}