package com.example.spotter.dto;

import com.example.spotter.dto.office.OfficeSummaryDTO;
import com.example.spotter.utils.enums.Role;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({ "id", "email", "firstName", "lastName", "role", "office","avatar","enabled", "createdAt" })
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private AttachmentDTO avatar;
    private OfficeSummaryDTO office;
    private Boolean enabled;
    private LocalDateTime createdAt;
}

