package com.example.spotter.controller;

import com.example.spotter.dto.auth.CreateUserDTO;
import com.example.spotter.model.UserEntity;
import com.example.spotter.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok(this.authService.login());
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody CreateUserDTO dto) {
        return ResponseEntity.ok(this.authService.register(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok(this.authService.logout());
    }

    @GetMapping("/user")
    public ResponseEntity<String> returnAuthenticatedUser() {
        return ResponseEntity.ok(authService.getAuthenticatedUser());
    }

}
