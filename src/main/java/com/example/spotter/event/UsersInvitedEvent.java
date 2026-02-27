package com.example.spotter.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UsersInvitedEvent {
    private List<UserInvitation> invitations;
}