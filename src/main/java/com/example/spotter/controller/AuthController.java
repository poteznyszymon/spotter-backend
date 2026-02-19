package com.example.spotter.controller;

import com.example.spotter.dto.UserDTO;
import com.example.spotter.dto.auth.AuthResponseDTO;
import com.example.spotter.dto.auth.LoginUserDTO;
import com.example.spotter.dto.auth.RegisterAdminDTO;
import com.example.spotter.dto.auth.VerifyUserDTO;
import com.example.spotter.model.UserEntity;
import com.example.spotter.service.AuthService;
import com.example.spotter.service.UserService;
import org.springframework.http.HttpStatus;
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
    public AuthResponseDTO login(@RequestBody LoginUserDTO dto) {
        return authService.login(dto);
    }

    @PostMapping("/register-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDTO register(@RequestBody RegisterAdminDTO dto) {
        return authService.registerAdmin(dto);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        authService.logout();
    }

    @GetMapping("/user")
    public UserDTO returnAuthenticatedUser(@AuthenticationPrincipal UserEntity user) {
        return userService.getUser(user.getId());
    }

    @PostMapping("/activate-user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateUser(@RequestBody VerifyUserDTO dto) {
        authService.activateUser(dto);
    }
}
