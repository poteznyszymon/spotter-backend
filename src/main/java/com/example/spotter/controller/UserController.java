package com.example.spotter.controller;

import com.example.spotter.model.UserEntity;
import com.example.spotter.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        return ResponseEntity.ok(userService.updateAvatar(userEntity.getId(), file));
    }

    @PostMapping("/invite")
    public ResponseEntity<Void> inviteUsers(@RequestBody List<String> emails) {
        return ResponseEntity.ok().build();
    }

}
