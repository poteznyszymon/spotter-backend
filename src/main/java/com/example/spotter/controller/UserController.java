package com.example.spotter.controller;

import com.example.spotter.model.UserEntity;
import com.example.spotter.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public String updateAvatar(@RequestParam MultipartFile file, @AuthenticationPrincipal UserEntity userEntity) {
        return userService.updateAvatar(userEntity.getId(), file);
    }

    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public void inviteUsers(@RequestBody List<String> emails, @AuthenticationPrincipal UserEntity user) {
        userService.inviteEmployeesAndNotify(user.getId(), emails);
    }

}
