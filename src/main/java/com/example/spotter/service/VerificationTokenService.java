package com.example.spotter.service;

import com.example.spotter.repository.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final Logger logger = LoggerFactory.getLogger(VerificationTokenService.class);

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public void deleteExpiredTokens() {
        int numberOfDeletedTokens = verificationTokenRepository.deleteExpiredTokens();
        logger.info("Deleted total {} expired tokens", numberOfDeletedTokens);
    }

}
