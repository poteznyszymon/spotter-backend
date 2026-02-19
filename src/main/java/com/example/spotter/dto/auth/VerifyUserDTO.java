package com.example.spotter.dto.auth;

import lombok.Data;

@Data
public class VerifyUserDTO {

    private String token;
    private String username;
    private String firstName;
    private String lastName;
    private String password;

}
