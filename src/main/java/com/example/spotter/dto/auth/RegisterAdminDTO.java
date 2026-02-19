package com.example.spotter.dto.auth;

import lombok.Data;

@Data
public class RegisterAdminDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String password;

}
