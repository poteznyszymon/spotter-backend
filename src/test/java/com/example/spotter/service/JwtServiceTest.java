package com.example.spotter.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET_KEY = "VGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JKd3RUZXN0aW5nMTIz";
    private static final long JWT_EXPIRATION = 1000L * 60 * 60; // 1 hour
    private UserDetails mockUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", JWT_EXPIRATION);

        mockUser = Mockito.mock(UserDetails.class);
        Mockito.when(mockUser.getUsername()).thenReturn("test@example.com");
    }

    @Test
    void getExpirationTimeReturnCorrectValue() {
        long returnedExpirationTime = jwtService.getExpirationTime();
        assertEquals(JWT_EXPIRATION, returnedExpirationTime);
        assertEquals(1, TimeUnit.MILLISECONDS.toHours(returnedExpirationTime));
    }

    @Nested
    class GenerateTokenTests {
        @Test
        void generateTokenReturnsTokenInValidFormat() {
            String token = jwtService.generateToken(mockUser);

            assertNotNull(token);
            assertFalse(token.isBlank());
            assertEquals(3, token.split("\\.").length);
        }

        @Test
        void generateTokenShouldIncludeExtraClaimsWhenMapProvided() {
            Map<String, Object> extraClaims = Map.of("role", "ADMIN", "age", 30);

            String token = jwtService.generateToken(extraClaims, mockUser);
            String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
            Integer age = jwtService.extractClaim(token, claims -> claims.get("age", Integer.class));

            assertNotNull(token);
            assertEquals("ADMIN", role);
            assertEquals(30, age);
        }
    }

    @Nested
    class ExtractClaimsTests {
        @Test
        void extractEmailReturnsValidEmail() {
            String token = jwtService.generateToken(mockUser);
            String extractedEmail = jwtService.extractEmail(token);

            assertNotNull(extractedEmail);
            assertEquals(mockUser.getUsername(), extractedEmail);
        }

        @Test
        void extractExpirationReturnsCorrectDate() {
            String token = jwtService.generateToken(mockUser);
            Date extractedExpiration = jwtService.extractExpiration(token);

            assertNotNull(extractedExpiration);

            assertTrue(extractedExpiration.after(new Date()));

            long expectedExpiration = System.currentTimeMillis() + JWT_EXPIRATION;
            long extractedExpirationMs = extractedExpiration.getTime();

            assertEquals(expectedExpiration, extractedExpirationMs, 1000);
        }

        @Test
        void extractEmailShouldThrowMalformedJwtExceptionWhenTokenIsMalformed() {
            String invalidToken = "not.valid.token";

            assertThrows(MalformedJwtException.class, () -> jwtService.extractEmail(invalidToken));
        }

        @Test
        void extractClaimShouldExtractCustomClaim() {
            Map<String, Object> extraClaims = Map.of("department", "IT", "level", 5);
            String token = jwtService.generateToken(extraClaims, mockUser);

            String department = jwtService.extractClaim(token, claims -> claims.get("department", String.class));
            Integer level = jwtService.extractClaim(token, claims -> claims.get("level", Integer.class));

            assertEquals("IT", department);
            assertEquals(5, level);
        }

        @Test
        void extractEmailShouldThrowSignatureExceptionWhenTokenSignatureIsTampered() {
            String validToken = jwtService.generateToken(mockUser);
            String temperedToken = validToken + "a";

            assertThrows(SignatureException.class, () -> jwtService.extractEmail(temperedToken));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void extractEmailShouldThrowIllegalArgumentExceptionWhenTokenIsNullOrEmpty(String token) {
            assertThrows(IllegalArgumentException.class, () -> jwtService.extractEmail(token));
        }

        @Test
        void extractEmailShouldThrowExpiredJwtExceptionWhenTokenIsExpired() {
            ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
            String token = jwtService.generateToken(mockUser);
            assertThrows(ExpiredJwtException.class, () -> jwtService.extractEmail(token));
        }

        @Test
        void extractEmailShouldThrowSignatureExceptionWhenSecretKeyIsDifferent() {
            String token = jwtService.generateToken(mockUser);
            String differentToken = "POdA5CIdxZ9ws7JUGD1TbSzqTar6HHF4hkYNLxQk4HOqdYVTXRx5aWkj78tHnwwuKT84w8PO4b86xXfBzveM1G";
            ReflectionTestUtils.setField(jwtService, "secretKey", differentToken);
            assertThrows(SignatureException.class, () -> jwtService.extractEmail(token));
        }
    }

    @Nested
    class IsTokenValidTests {
        @Test
        void isTokenValidReturnsTrueWhenUserMatches() {
            String token = jwtService.generateToken(mockUser);
            assertTrue(jwtService.isTokenValid(token, mockUser));
        }

        @Test
        void isTokenValidReturnsFalseWhenUserDoesNotMatch() {
            UserDetails differentUser = Mockito.mock(UserDetails.class);
            Mockito.when(differentUser.getUsername()).thenReturn("invalid@example.com");
            String token = jwtService.generateToken(mockUser);

            assertFalse(jwtService.isTokenValid(token, differentUser));
        }

        @Test
        void isTokenValidShouldThrowExpiredJwtExceptionWhenTokenIsExpired() {
            ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
            String token = jwtService.generateToken(mockUser);
            assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, mockUser));
        }
    }

    @Nested
    class EdgeCaseTests {
        @Test
        void extractEmailShouldHandleUsernameWithSpecialCharacters() {
            UserDetails user = Mockito.mock(UserDetails.class);
            String expectedEmail = "user.!#$%&'*+-/=?^_{|}~`test@example.com";
            Mockito.when(user.getUsername()).thenReturn(expectedEmail);
            String token = jwtService.generateToken(user);
            assertEquals(expectedEmail, jwtService.extractEmail(token));
        }

        @Test
        void generateTokenShouldThrowNullPointerExceptionWhenUserDetailsIsNull() {
            assertThrows(NullPointerException.class, () -> jwtService.generateToken(null));
        }
    }

}