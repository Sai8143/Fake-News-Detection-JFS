package com.truthlens.dto;

import java.util.ArrayList;
import java.util.List;

public class UserProfileDto {
    private String userId = "default";
    private String name = "News Reader";
    private List<String> interests = new ArrayList<>(List.of("technology", "science", "health"));
    private int scanned = 0;
    private int fakeDetected = 0;
    private String apiKey = "";

    public UserProfileDto() {
    }

    public UserProfileDto(String userId, String name, List<String> interests, int scanned, int fakeDetected, String apiKey) {
        this.userId = userId;
        this.name = name;
        this.interests = interests != null ? interests : new ArrayList<>();
        this.scanned = scanned;
        this.fakeDetected = fakeDetected;
        this.apiKey = apiKey != null ? apiKey : "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public int getScanned() {
        return scanned;
    }

    public void setScanned(int scanned) {
        this.scanned = scanned;
    }

    public int getFakeDetected() {
        return fakeDetected;
    }

    public void setFakeDetected(int fakeDetected) {
        this.fakeDetected = fakeDetected;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
