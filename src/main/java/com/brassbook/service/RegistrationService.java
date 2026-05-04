package com.brassbook.service;

import com.brassbook.dto.request.CodeRequest;
import com.brassbook.dto.request.PasswordRequest;
import com.brassbook.dto.request.RegistrationRequest;
import com.brassbook.dto.response.RegistrationResponse;
import com.brassbook.entity.User;
import com.brassbook.enums.UserRole;
import com.brassbook.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    public RegistrationResponse createUser(RegistrationRequest userToCreate) {
        if (userRepository.existsByEmail(userToCreate.getEmail())) {
            throw new IllegalArgumentException("Such an email already exists");
        }
        if (!isPasswordValid(userToCreate.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        if (userToCreate.getRoleName() == UserRole.ROLE_ANONYMOUS) {
            throw new IllegalArgumentException("Invalid user role");
        }
        if (userToCreate.getRoleName() == UserRole.ROLE_COMPANY && isInvalidCompanyUser(userToCreate)) {
            throw new IllegalArgumentException("Invalid data from a company user");
        }

        String savedCode = redisTemplate.opsForValue().get(userToCreate.getEmail());
        if (savedCode == null || !savedCode.equals(userToCreate.getCode())) {
            throw new IllegalArgumentException("Invalid code");
        }

        User newUser = userDetailsService.createNewUser(userToCreate);
        return new RegistrationResponse(newUser.getId());
    }

    public void sendCode(CodeRequest codeRequest) {
        if (userRepository.existsByEmail(codeRequest.getEmail())) {
            throw new IllegalArgumentException("Such an email already exists");
        }
        if (!codeRequest.getIsConfirmed()) {
            throw new IllegalArgumentException("User did not give an agreement");
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        redisTemplate.opsForValue().set(
                codeRequest.getEmail(),
                code,
                Duration.ofMinutes(3)
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(codeRequest.getEmail());
        message.setSubject("Код подтверждения регистрации в BrassBook");
        message.setText("Ваш код: " + code);

        mailSender.send(message);
    }

    public void updatePassword(PasswordRequest passwordRequest) {
        User updateUser = userRepository.findById(passwordRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found user by id = " + passwordRequest.getId()));
        if (!isPasswordValid(passwordRequest.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        updateUser.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
        userRepository.save(updateUser);
    }

    private boolean isPasswordValid(String password) {
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasSymbol = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSymbol = true;
            }

            if (hasLower && hasUpper && hasSymbol) {
                return true;
            }
        }

        return hasLower && hasUpper && hasSymbol;
    }

    private boolean isInvalidCompanyUser(RegistrationRequest user) {
        if (user.getInn() == null) {
            return true;
        }
        int innLength = user.getInn().toString().length();
        return user.getFirstName() == null || user.getLastName() == null ||
                user.getCompanyName() == null || user.getProfession() == null ||
                innLength != 10 && innLength != 12;
    }
}
