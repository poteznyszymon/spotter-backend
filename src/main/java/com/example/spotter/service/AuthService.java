package com.example.spotter.service;

import com.example.spotter.dto.auth.AuthResponseDTO;
import com.example.spotter.dto.auth.LoginUserDTO;
import com.example.spotter.dto.auth.RegisterAdminDTO;
import com.example.spotter.dto.auth.VerifyUserDTO;
import com.example.spotter.event.AdminRegisteredEvent;
import com.example.spotter.exception.exceptions.EntityNotFoundException;
import com.example.spotter.exception.exceptions.UserAlreadyExistsException;
import com.example.spotter.exception.exceptions.UserNotFoundException;
import com.example.spotter.model.UserEntity;
import com.example.spotter.model.VerificationTokenEntity;
import com.example.spotter.repository.UserRepository;
import com.example.spotter.repository.VerificationTokenRepository;
import com.example.spotter.utils.enums.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    @Value("${app.jwt.access-token-name}")
    private String accessTokenName;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final HttpServletResponse response;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final VerificationTokenRepository verificationTokenRepository;
    private final OfficeService officeService;

    private final int cookieMaxAge =  24 * 60 * 60; // 1 day

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            HttpServletResponse response,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher,
            VerificationTokenRepository verificationTokenRepository,
            OfficeService officeService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.response = response;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.verificationTokenRepository = verificationTokenRepository;
        this.officeService = officeService;
    }

    public AuthResponseDTO login(LoginUserDTO dto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserEntity user = (UserEntity) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        setTokenToCookie(token);
        return new AuthResponseDTO(token, jwtService.getExpirationTime());
    }

    @Transactional
    public AuthResponseDTO registerAdmin(RegisterAdminDTO dto) {

        if (userRepository.existsUserEntityByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email already taken");
        }

        UserEntity user = UserEntity.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.ADMIN)
                .build();

        UserEntity savedUser = userRepository.save(user);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(savedUser);
        setTokenToCookie(token);
        officeService.createOffice(savedUser);
        return new AuthResponseDTO(token, jwtService.getExpirationTime());
    }

    public void logout() {
        clearTokenCookie();
    }

    @Transactional
    public void activateUser(VerifyUserDTO dto) {
        VerificationTokenEntity tokenEntity = verificationTokenRepository
                .findByToken(dto.getToken())
                .orElseThrow(() -> new IllegalStateException("Invalid token"));

        if (tokenEntity.isExpired()) {
            throw new IllegalStateException("Token expired");
        }

        UserEntity user = Optional.ofNullable(tokenEntity.getUser())
                .orElseThrow(() -> new UserNotFoundException("User assigned to the token not found"));

        if (user.isEnabled()) {
            throw new IllegalStateException("User already verified");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);

        verificationTokenRepository.delete(tokenEntity);
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
