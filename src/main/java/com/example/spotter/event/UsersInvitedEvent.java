package com.example.spotter.event;

import java.util.List;

public record UsersInvitedEvent(List<UserInvitation> invitations) {
}