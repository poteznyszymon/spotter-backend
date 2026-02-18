package com.example.spotter.scheduling;

import com.example.spotter.service.VerificationTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SchedulingTasks {

    private final VerificationTokenService verificationTokenService;

    public SchedulingTasks(VerificationTokenService verificationTokenService) {
        this.verificationTokenService = verificationTokenService;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Warsaw")
    public void clearExpiredTokens() {
        verificationTokenService.deleteExpiredTokens();
    }

}
