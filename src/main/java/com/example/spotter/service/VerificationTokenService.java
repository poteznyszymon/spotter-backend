package com.example.spotter.service;

import com.example.spotter.event.UserInvitation;
import com.example.spotter.event.UsersInvitedEvent;
import com.example.spotter.model.UserEntity;
import com.example.spotter.model.VerificationTokenEntity;
import com.example.spotter.repository.VerificationTokenRepository;
import com.example.spotter.utils.enums.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class VerificationTokenService {

    @Value("${app.verification-token-expiration-time-hours:24}")
    long tokenExpirationTimeHours;

    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository, ApplicationEventPublisher eventPublisher) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void deleteExpiredTokens() {
        int numberOfDeletedTokens = verificationTokenRepository.deleteExpiredTokens();
        log.info("Deleted total {} expired tokens", numberOfDeletedTokens);
    }

    @Transactional
    public void createTokensAndNotify(List<UserEntity> users) {
        List<VerificationTokenEntity> tokensToSave = new ArrayList<>();
        List<UserInvitation> payload = new ArrayList<>();
        for (UserEntity user : users) {
            String token = UUID.randomUUID().toString();
            tokensToSave.add(VerificationTokenEntity.builder()
                    .token(token)
                    .user(user)
                    .tokenType(TokenType.ACTIVATION)
                    .expiryDate(LocalDateTime.now().plusHours(tokenExpirationTimeHours))
                    .build());
            payload.add(new UserInvitation(user.getEmail(), token));
        }
        verificationTokenRepository.saveAll(tokensToSave);
        eventPublisher.publishEvent(new UsersInvitedEvent(payload));
    }

}
