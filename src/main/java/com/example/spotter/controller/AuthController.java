package com.example.spotter.controller;

import com.example.spotter.dto.UserDTO;
import com.example.spotter.dto.auth.AuthResponseDTO;
import com.example.spotter.dto.auth.LoginUserDTO;
import com.example.spotter.dto.auth.RegisterUserDTO;
import com.example.spotter.mapper.UserMapper;
import com.example.spotter.model.UserEntity;
import com.example.spotter.service.AuthService;
import com.example.spotter.utils.enums.ModelConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    
    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginUserDTO dto) {
        return ResponseEntity.ok(this.authService.login(dto));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterUserDTO dto) {
        return ResponseEntity.ok(this.authService.register(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        this.authService.logout();
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> returnAuthenticatedUser(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
