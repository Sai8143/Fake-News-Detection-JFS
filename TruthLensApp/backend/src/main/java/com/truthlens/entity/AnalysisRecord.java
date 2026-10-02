package com.truthlens.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_records")
public class AnalysisRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2048)
    private String url;

    @Column(length = 1000)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentText;

    @Column(nullable = false, length = 50)
    private String verdict;

    @Column(nullable = false, length = 50)
    private String verdictClass;

    private int score;
    private int heuristicScore;
    private Integer mlScore;

    @Column(length = 255)
    private String domain;

    @Column(length = 2000)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String signalsJson;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AnalysisRecord() {
    }

    public AnalysisRecord(String url, String title, String contentText, String verdict,
                          String verdictClass, int score, int heuristicScore, Integer mlScore,
                          String domain, String summary, String signalsJson) {
        this.url = url;
        this.title = title;
        this.contentText = contentText;
        this.verdict = verdict;
        this.verdictClass = verdictClass;
        this.score = score;
        this.heuristicScore = heuristicScore;
        this.mlScore = mlScore;
        this.domain = domain;
        this.summary = summary;
        this.signalsJson = signalsJson;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getVerdictClass() {
        return verdictClass;
    }

    public void setVerdictClass(String verdictClass) {
        this.verdictClass = verdictClass;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHeuristicScore() {
        return heuristicScore;
    }

    public void setHeuristicScore(int heuristicScore) {
        this.heuristicScore = heuristicScore;
    }

    public Integer getMlScore() {
        return mlScore;
    }

    public void setMlScore(Integer mlScore) {
        this.mlScore = mlScore;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSignalsJson() {
        return signalsJson;
    }

    public void setSignalsJson(String signalsJson) {
        this.signalsJson = signalsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
