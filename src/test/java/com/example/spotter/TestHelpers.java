package com.example.spotter;

import com.example.spotter.model.UserEntity;
import com.example.spotter.model.VerificationTokenEntity;
import com.example.spotter.utils.enums.TokenType;

import java.time.LocalDateTime;

public class TestHelpers {

    public UserEntity createUserEntity(String email, boolean enabled) {
        return UserEntity.builder()
                .email(email)
                .enabled(enabled)
                .build();
    }

    public VerificationTokenEntity createTokenEntity(String token, UserEntity user) {
        return VerificationTokenEntity.builder()
                .token(token)
                .user(user)
                .tokenType(TokenType.ACTIVATION)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
    }
}
