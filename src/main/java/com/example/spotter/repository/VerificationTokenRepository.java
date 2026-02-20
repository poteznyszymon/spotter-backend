package com.example.spotter.repository;

import com.example.spotter.model.UserEntity;
import com.example.spotter.model.VerificationTokenEntity;
import com.example.spotter.utils.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Long> {
    @Modifying
    @Query("DELETE FROM VerificationTokenEntity t WHERE t.expiryDate < CURRENT_TIMESTAMP ")
    int deleteExpiredTokens(); // returns number of deleted tokens

    Optional<VerificationTokenEntity> findByToken(String token);

    @Modifying
    @Query("DELETE FROM VerificationTokenEntity t WHERE t.user.id = :userId AND t.tokenType = :tokenType")
    int deleteUserTokens(@Param("userId") Long userId, @Param("tokenType") TokenType tokenType);

    Long user(UserEntity user);
}
