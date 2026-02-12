package com.example.spotter.service;

import com.example.spotter.dto.auth.AuthResponseDTO;
import com.example.spotter.dto.auth.LoginUserDTO;
import com.example.spotter.dto.auth.RegisterUserDTO;
import com.example.spotter.exception.exceptions.UserAlreadyExistsException;
import com.example.spotter.model.UserEntity;
import com.example.spotter.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Value("${app.jwt.access-token-name}")
    private String accessTokenName;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final HttpServletResponse response;
    private final PasswordEncoder passwordEncoder;

    private final int cookieMaxAge =  24 * 60 * 60; // 1 day

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService,
            AuthenticationManager authenticationManager, HttpServletResponse response, PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.response = response;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO login(LoginUserDTO dto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserEntity user = (UserEntity) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        setTokenToCookie(token);
        return new AuthResponseDTO(token, jwtService.getExpirationTime());
    }

    public AuthResponseDTO register(RegisterUserDTO dto) {

        if (userRepository.existsUserEntityByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email already taken");
        }

        if (userRepository.existsUserEntityByUsername(dto.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken");
        }

        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        UserEntity savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(savedUser);
        setTokenToCookie(token);
        return new AuthResponseDTO(token, jwtService.getExpirationTime());
    }

    public void logout() {
        clearTokenCookie();
    }

    private void setTokenToCookie(String token) {
        Cookie cookie = new Cookie(accessTokenName, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(cookieMaxAge);
        response.addCookie(cookie);
    }

    private void clearTokenCookie() {
        Cookie cookie = new Cookie(accessTokenName, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
