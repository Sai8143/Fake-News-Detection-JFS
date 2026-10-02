package com.truthlens.service;

import com.truthlens.dto.UserProfileDto;
import com.truthlens.entity.UserProfile;
import com.truthlens.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final UserProfileRepository userProfileRepository;

    public ProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(String userId) {
        String safeId = (userId == null || userId.trim().isEmpty()) ? "default" : userId.trim();
        Optional<UserProfile> opt = userProfileRepository.findByUserId(safeId);

        if (opt.isPresent()) {
            UserProfile profile = opt.get();
            List<String> interests = parseInterests(profile.getInterests());
            return new UserProfileDto(
                    profile.getUserId(),
                    profile.getName(),
                    interests,
                    profile.getScannedCount(),
                    profile.getFakeDetectedCount(),
                    profile.getGnewsApiKey()
            );
        } else {
            // Return default profile
            return new UserProfileDto(
                    safeId,
                    "News Reader",
                    new ArrayList<>(List.of("technology", "science", "health")),
                    0,
                    0,
                    ""
            );
        }
    }

    @Transactional
    public UserProfileDto saveProfile(String userId, UserProfileDto dto) {
        String safeId = (userId != null && !userId.trim().isEmpty()) ? userId.trim() :
                (dto.getUserId() != null && !dto.getUserId().trim().isEmpty() ? dto.getUserId().trim() : "default");

        UserProfile profile = userProfileRepository.findByUserId(safeId)
                .orElse(new UserProfile(safeId, "News Reader", "technology,science,health", 0, 0, ""));

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            profile.setName(dto.getName().trim());
        }
        if (dto.getInterests() != null) {
            profile.setInterests(String.join(",", dto.getInterests()));
        }
        if (dto.getApiKey() != null) {
            profile.setGnewsApiKey(dto.getApiKey().trim());
        }
        if (dto.getScanned() > 0) {
            profile.setScannedCount(dto.getScanned());
        }
        if (dto.getFakeDetected() > 0) {
            profile.setFakeDetectedCount(dto.getFakeDetected());
        }

        profile = userProfileRepository.save(profile);

        return new UserProfileDto(
                profile.getUserId(),
                profile.getName(),
                parseInterests(profile.getInterests()),
                profile.getScannedCount(),
                profile.getFakeDetectedCount(),
                profile.getGnewsApiKey()
        );
    }

    @Transactional
    public void incrementStats(String userId, boolean isFake) {
        String safeId = (userId == null || userId.trim().isEmpty()) ? "default" : userId.trim();
        UserProfile profile = userProfileRepository.findByUserId(safeId)
                .orElseGet(() -> new UserProfile(safeId, "News Reader", "technology,science,health", 0, 0, ""));

        profile.setScannedCount(profile.getScannedCount() + 1);
        if (isFake) {
            profile.setFakeDetectedCount(profile.getFakeDetectedCount() + 1);
        }
        userProfileRepository.save(profile);
    }

    private List<String> parseInterests(String interestsStr) {
        if (interestsStr == null || interestsStr.trim().isEmpty()) {
            return new ArrayList<>(List.of("technology", "science", "health"));
        }
        return Arrays.stream(interestsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
