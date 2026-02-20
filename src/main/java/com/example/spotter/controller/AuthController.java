package com.example.spotter.controller;

import com.example.spotter.dto.UserDTO;
import com.example.spotter.dto.auth.AuthResponseDTO;
import com.example.spotter.dto.auth.LoginUserDTO;
import com.example.spotter.dto.auth.RegisterAdminDTO;
import com.example.spotter.dto.auth.VerifyUserDTO;
import com.example.spotter.model.UserEntity;
import com.example.spotter.service.AuthService;
import com.example.spotter.service.UserService;
import com.example.spotter.service.VerificationTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;

    public AuthController(AuthService authService, UserService userService, VerificationTokenService verificationTokenService) {
        this.authService = authService;
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
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

    @PostMapping("/resend-verification-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendToken(@RequestParam("userId") long userID) {
        authService.resendActivationToken(userID);
    }

    @GetMapping("/verify-token")
    public String verifyToken(@RequestParam("token") String token) {
        return verificationTokenService.verifyToken(token).getUserEntity().getEmail();
    }
}
