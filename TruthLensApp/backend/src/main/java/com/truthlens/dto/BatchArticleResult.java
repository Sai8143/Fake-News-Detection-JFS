package com.truthlens.dto;

public class BatchArticleResult {
    private String title;
    private String url;
    private String domain;
    private int score;
    private String verdict;
    private String verdictClass;

    public BatchArticleResult() {
    }

    public BatchArticleResult(String title, String url, String domain, int score, String verdict, String verdictClass) {
        this.title = title;
        this.url = url;
        this.domain = domain;
        this.score = score;
        this.verdict = verdict;
        this.verdictClass = verdictClass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
}
