package com.example.spotter.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInvitation {
    private String email;
    private String token;
}
