package com.example.spotter.dto;

import com.example.spotter.utils.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
    private String avatarUrl;
    private LocalDateTime createdAt;
}
