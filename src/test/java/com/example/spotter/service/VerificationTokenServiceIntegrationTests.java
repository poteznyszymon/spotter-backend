package com.example.spotter.service;

import com.example.spotter.AbstractIntegrationTest;
import com.example.spotter.TestHelpers;
import com.example.spotter.event.UserInvitation;
import com.example.spotter.model.UserEntity;
import com.example.spotter.model.VerificationTokenEntity;
import com.example.spotter.repository.UserRepository;
import com.example.spotter.repository.VerificationTokenRepository;
import com.example.spotter.utils.enums.TokenType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class VerificationTokenServiceIntegrationTests extends AbstractIntegrationTest {

    private final TestHelpers helpers = new TestHelpers();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Nested
    class CreateTokensTests {
        @Test
        void shouldCreateAndSaveTokensToDatabase() {
            UserEntity user1 = helpers.createUserEntity("user1@example.com", false);
            UserEntity user2 = helpers.createUserEntity("user2@example.com", false);
            List<UserEntity> savedUsers = userRepository.saveAll(List.of(user1, user2));

            List<UserInvitation> response = verificationTokenService.createTokens(savedUsers, TokenType.ACTIVATION);
            assertEquals(2, response.size());
            assertNotNull(response.get(0).token());
            assertNotNull(response.get(1).token());
            assertTrue(response.stream().anyMatch(inv -> inv.email().equals("user1@example.com") || inv.email().equals("user2@example.com")));

            List<VerificationTokenEntity> savedTokens = verificationTokenRepository.findAll();
            assertEquals(2, savedTokens.size());

            VerificationTokenEntity firstTokenEntity = savedTokens.get(0);
            assertNotNull(firstTokenEntity.getToken());
            assertEquals(TokenType.ACTIVATION, firstTokenEntity.getTokenType());
            assertNotNull(firstTokenEntity.getExpiryDate());
            assertNotNull(firstTokenEntity.getUser());

            VerificationTokenEntity secondTokenEntity = savedTokens.get(1);
            assertNotNull(secondTokenEntity.getToken());
            assertEquals(TokenType.ACTIVATION, secondTokenEntity.getTokenType());
            assertNotNull(secondTokenEntity.getExpiryDate());
            assertNotNull(secondTokenEntity.getUser());

            assertTrue(savedTokens.stream().anyMatch(inv -> inv.getUser().getEmail().equals("user1@example.com") || inv.getUser().getEmail().equals("user2@example.com")));
        }

        @Test
        void shouldDoNothingWhenEmptyUserListProvided() {
            List<UserInvitation> response = verificationTokenService.createTokens(List.of(), TokenType.ACTIVATION);
            assertTrue(response.isEmpty());
            assertEquals(0, verificationTokenRepository.findAll().size());
        }
    }

}
