package com.example.spotter.controller;

import com.example.spotter.dto.UserDTO;
import com.example.spotter.dto.auth.AuthResponseDTO;
import com.example.spotter.dto.auth.LoginUserDTO;
import com.example.spotter.dto.auth.RegisterAdminDTO;
import com.example.spotter.mapper.UserMapper;
import com.example.spotter.model.UserEntity;
import com.example.spotter.service.AuthService;
import com.example.spotter.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginUserDTO dto) {
        return ResponseEntity.ok(this.authService.login(dto));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterAdminDTO dto) {
        return new ResponseEntity<>(this.authService.registerAdmin(dto), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        this.authService.logout();
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> returnAuthenticatedUser(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(userService.getUser(user.getId()));
    }
}
