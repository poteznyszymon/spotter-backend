package com.example.spotter.service;

import com.example.spotter.TestHelpers;
import com.example.spotter.dto.VerifyTokenResponseDTO;
import com.example.spotter.event.UserInvitation;
import com.example.spotter.exception.exceptions.UserNotFoundException;
import com.example.spotter.model.UserEntity;
import com.example.spotter.model.VerificationTokenEntity;
import com.example.spotter.repository.VerificationTokenRepository;
import com.example.spotter.utils.enums.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceTest {

    private final TestHelpers helpers = new TestHelpers();

    @Mock
    private VerificationTokenRepository repository;

    @InjectMocks
    private VerificationTokenService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "tokenExpirationTimeHours", 24);
    }

    @Nested
    class createTokensTests {
        @Test
        void createTokensGeneratesUniqueTokensForEachUser() {
            UserEntity user1 = helpers.createUserEntity("user1@example.com", false);
            UserEntity user2 = helpers.createUserEntity("user2@example.com", false);
            List<UserEntity> users = List.of(user1, user2);
            Mockito.when(repository.saveAll(Mockito.any())).thenReturn(List.of());

            List<UserInvitation> response = service.createTokens(users, TokenType.ACTIVATION);

            String token1 = response.get(0).getToken();
            String token2 = response.get(1).getToken();
            assertNotNull(token1);
            assertNotNull(token2);
            assertNotEquals(token1, token2);
        }

        @Test
        void createTokensReturnsPayloadWithCorrectEmails() {
            UserEntity user1 = helpers.createUserEntity("user1@example.com", false);
            UserEntity user2 = helpers.createUserEntity("user2@example.com", false);
            List<UserEntity> users = List.of(user1, user2);
            Mockito.when(repository.saveAll(Mockito.any())).thenReturn(List.of());

            List<UserInvitation> response = service.createTokens(users, TokenType.ACTIVATION);

            assertEquals(2, response.size());

            List<String> emails = response.stream()
                    .map(UserInvitation::getEmail)
                    .toList();

            assertTrue(emails.contains(user1.getEmail()));
            assertTrue(emails.contains(user2.getEmail()));
        }
    }

    @Nested
    class VerifyTokenTests {
        @Test
        void verifyTokenThrowsExceptionWhenTokenDoesNotExists() {
            String invalidToken = "non-existing-token";
            Mockito.when(repository.findByToken(invalidToken))
                    .thenReturn(Optional.empty());

            IllegalStateException thrownException = assertThrows(IllegalStateException.class, () -> service.verifyToken(invalidToken));

            assertEquals("Invalid token", thrownException.getMessage());
        }

        @Test
        void verifyTokenThrowsExceptionWhenTokenIsExpired() {
            String expiredToken = "expired-token";
            UserEntity user = helpers.createUserEntity("test@example.com", false);
            VerificationTokenEntity expiredTokenEntity = helpers.createTokenEntity(expiredToken, user);
            expiredTokenEntity.setExpiryDate(LocalDateTime.now().minusHours(1));
            Mockito.when(repository.findByToken(expiredToken))
                    .thenReturn(Optional.of(expiredTokenEntity));

            IllegalStateException thrownException = assertThrows(IllegalStateException.class, () -> service.verifyToken(expiredToken));
            assertEquals("Token expired", thrownException.getMessage());
        }

        @Test
        void verifyTokenThrowsExceptionWhenNoUserFoundInTokenEntity() {
            String token = "valid-token";
            VerificationTokenEntity tokenEntity = helpers.createTokenEntity(token, null);
            Mockito.when(repository.findByToken(token))
                    .thenReturn(Optional.of(tokenEntity));

            UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> service.verifyToken(token));
            assertEquals("User assigned to the token not found", thrownException.getMessage());
        }

        @Test
        void verifyTokenThrowsExceptionWhenUserIsAlreadyEnabled() {
            String token = "valid-token";
            UserEntity enabledUser = helpers.createUserEntity("test@example.com", true);
            VerificationTokenEntity tokenEntity = helpers.createTokenEntity(token, enabledUser);
            Mockito.when(repository.findByToken(token))
                    .thenReturn(Optional.of(tokenEntity));

            IllegalStateException thrownException = assertThrows(IllegalStateException.class, () -> service.verifyToken(token));
            assertEquals("User already verified", thrownException.getMessage());
        }

        @Test
        void verifyTokenReturnVerifyTokenResponseWhenTokenIsValid() {
            String token = "valid-token";
            UserEntity user = helpers.createUserEntity("test@example.com", false);
            VerificationTokenEntity tokenEntity = helpers.createTokenEntity(token, user);
            Mockito.when(repository.findByToken(token))
                    .thenReturn(Optional.of(tokenEntity));

            VerifyTokenResponseDTO response = service.verifyToken(token);

            assertEquals(user.getEmail(), response.getUserEntity().getEmail());
            assertEquals(token, response.getTokenEntity().getToken());
        }


    }


}