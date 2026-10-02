package com.truthlens.dto;

import java.util.List;

public class AnalyzeResponse {
    private Long id;
    private String verdict;
    private String verdictClass;
    private int score;
    private int heuristicScore;
    private Integer mlScore;
    private List<SignalDto> signals;
    private String summary;
    private String domain;
    private String timestamp;

    public AnalyzeResponse() {
    }

    public AnalyzeResponse(Long id, String verdict, String verdictClass, int score,
                           int heuristicScore, Integer mlScore, List<SignalDto> signals,
                           String summary, String domain, String timestamp) {
        this.id = id;
        this.verdict = verdict;
        this.verdictClass = verdictClass;
        this.score = score;
        this.heuristicScore = heuristicScore;
        this.mlScore = mlScore;
        this.signals = signals;
        this.summary = summary;
        this.domain = domain;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<SignalDto> getSignals() {
        return signals;
    }

    public void setSignals(List<SignalDto> signals) {
        this.signals = signals;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
