package com.example.spotter.service;

import com.example.spotter.dto.VerifyTokenResponseDTO;
import com.example.spotter.event.UserInvitation;
import com.example.spotter.exception.exceptions.UserNotFoundException;
import com.example.spotter.model.UserEntity;
import com.example.spotter.model.VerificationTokenEntity;
import com.example.spotter.repository.VerificationTokenRepository;
import com.example.spotter.utils.enums.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class VerificationTokenService {

    @Value("${app.verification-token-expiration-time-hours:24}")
    long tokenExpirationTimeHours;

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public void deleteExpiredTokens() {
        int numberOfDeletedTokens = verificationTokenRepository.deleteExpiredTokens();
        log.info("Deleted total {} expired tokens", numberOfDeletedTokens);
    }

    @Transactional
    public List<UserInvitation> createTokens(List<UserEntity> users, TokenType tokenType) {
        List<VerificationTokenEntity> tokensToSave = new ArrayList<>();
        List<UserInvitation> payload = new ArrayList<>();
        for (UserEntity user : users) {
            String token = UUID.randomUUID().toString();
            tokensToSave.add(VerificationTokenEntity.builder()
                    .token(token)
                    .user(user)
                    .tokenType(tokenType)
                    .expiryDate(LocalDateTime.now().plusHours(tokenExpirationTimeHours))
                    .build());
            payload.add(UserInvitation.builder()
                            .email(user.getEmail())
                            .token(token)
                            .build());
        }
        verificationTokenRepository.saveAll(tokensToSave);
        return payload;
    }

    public VerifyTokenResponseDTO verifyToken(String token) {
        VerificationTokenEntity tokenEntity = verificationTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Invalid token"));

        if (tokenEntity.isExpired()) {
            throw new IllegalStateException("Token expired");
        }

        UserEntity user = Optional.ofNullable(tokenEntity.getUser())
                .orElseThrow(() -> new UserNotFoundException("User assigned to the token not found"));

        if (user.isEnabled()) {
            throw new IllegalStateException("User already verified");
        }

        return new VerifyTokenResponseDTO(user, tokenEntity);
    }

    public void deleteUserTokens(Long userId, TokenType tokenType) {
        int totalDeletedTokens = verificationTokenRepository.deleteUserTokens(userId, tokenType);
        log.info("Deleted total {} tokens of type {}", totalDeletedTokens, tokenType.name());
    }

}
