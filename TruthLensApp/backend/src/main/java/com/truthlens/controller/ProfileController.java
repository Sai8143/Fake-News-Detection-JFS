package com.truthlens.controller;

import com.truthlens.dto.UserProfileDto;
import com.truthlens.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile(
            @RequestParam(name = "uid", defaultValue = "default") String userId) {
        UserProfileDto profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> saveProfile(
            @RequestParam(name = "uid", defaultValue = "default") String userId,
            @RequestBody UserProfileDto dto) {
        UserProfileDto saved = profileService.saveProfile(userId, dto);
        return ResponseEntity.ok(Map.of(
                "status", "saved",
                "profile", saved
        ));
    }
}
