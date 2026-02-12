package com.example.spotter.controller;

import com.example.spotter.model.UserEntity;
import com.example.spotter.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/avatar")
    public ResponseEntity<String> updateAvatar(
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal UserEntity userEntity
    ) {
        String url =  userService.updateAvatar(userEntity.getId(), file);
        return ResponseEntity.ok(url);
    }

}
