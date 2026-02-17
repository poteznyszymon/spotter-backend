package com.example.spotter.event;

import com.example.spotter.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminRegisteredEvent {
    private UserEntity admin;
}
