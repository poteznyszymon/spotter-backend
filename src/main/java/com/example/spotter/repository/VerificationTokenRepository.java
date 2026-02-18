package com.example.spotter.repository;

import com.example.spotter.model.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Long> {
    @Modifying
    @Query("DELETE FROM VerificationTokenEntity t WHERE t.expiryDate < CURRENT_TIMESTAMP ")
    int deleteExpiredTokens(); // returns number of deleted tokens
}
