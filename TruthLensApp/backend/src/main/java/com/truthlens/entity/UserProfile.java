package com.truthlens.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(length = 100)
    private String userId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String interests; // Comma-separated: e.g. "technology,science,health"

    private int scannedCount;
    private int fakeDetectedCount;

    @Column(length = 255)
    private String gnewsApiKey;

    private LocalDateTime updatedAt;

    public UserProfile() {
    }

    public UserProfile(String userId, String name, String interests, int scannedCount, int fakeDetectedCount, String gnewsApiKey) {
        this.userId = userId;
        this.name = name;
        this.interests = interests;
        this.scannedCount = scannedCount;
        this.fakeDetectedCount = fakeDetectedCount;
        this.gnewsApiKey = gnewsApiKey;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public int getScannedCount() {
        return scannedCount;
    }

    public void setScannedCount(int scannedCount) {
        this.scannedCount = scannedCount;
    }

    public int getFakeDetectedCount() {
        return fakeDetectedCount;
    }

    public void setFakeDetectedCount(int fakeDetectedCount) {
        this.fakeDetectedCount = fakeDetectedCount;
    }

    public String getGnewsApiKey() {
        return gnewsApiKey;
    }

    public void setGnewsApiKey(String gnewsApiKey) {
        this.gnewsApiKey = gnewsApiKey;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
