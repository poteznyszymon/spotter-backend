package com.example.spotter.controller;

import com.example.spotter.dto.auth.LoginUserDTO;
import com.example.spotter.dto.auth.RegisterUserDTO;
import com.example.spotter.model.UserEntity;
import com.example.spotter.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserEntity> login(@RequestBody LoginUserDTO dto) {
        return ResponseEntity.ok(this.authService.login(dto));
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody RegisterUserDTO dto) {
        return ResponseEntity.ok(this.authService.register(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        this.authService.logout();
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/user")
    public ResponseEntity<UserEntity> returnAuthenticatedUser(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(user);
    }
}
