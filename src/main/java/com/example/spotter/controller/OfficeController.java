package com.example.spotter.controller;

import com.example.spotter.dto.UserSummaryDTO;
import com.example.spotter.model.UserEntity;
import com.example.spotter.service.OfficeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/office")
public class OfficeController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @GetMapping("/users")
    public List<UserSummaryDTO> getUsers(@AuthenticationPrincipal UserEntity user) {
        return officeService.getUsers(user);
    }

}
