package com.example.spotter.dto;

import com.example.spotter.utils.enums.Role;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({ "id", "email", "firstName", "lastName", "role", "avatar","enabled", "createdAt" })
public class UserSummaryDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private AttachmentDTO avatar;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
