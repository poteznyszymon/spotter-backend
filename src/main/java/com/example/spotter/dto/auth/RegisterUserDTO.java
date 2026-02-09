package com.example.spotter.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RegisterUserDTO {

    private String email;
    private String firstName;
    private String password;
    private String lastName;
    private String username;

}
