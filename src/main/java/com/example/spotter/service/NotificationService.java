package com.example.spotter.service;

import com.example.spotter.event.UserInvitation;
import com.example.spotter.event.UsersInvitedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUsersInvitations(UsersInvitedEvent event) {
        for (UserInvitation invitation : event.invitations()) {
            try {
                emailService.sendPlanText(invitation.email(), "Activate your account", invitation.token());
            } catch (Exception e) {
                log.error("Unabled to send invitation email for {}", invitation.email());
            }
        }
    }
}
