package com.example.spotter.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserDTO {

    private String email;
    private String firstName;
    private String password;
    private String lastName;
    private String username;

}
