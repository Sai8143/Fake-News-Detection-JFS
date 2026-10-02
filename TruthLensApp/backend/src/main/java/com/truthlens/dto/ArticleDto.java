package com.truthlens.dto;

public class ArticleDto {
    private String id;
    private String title;
    private String source;
    private String url;
    private String publishedAt;
    private String description;
    private String image;
    private int score;
    private String verdict;
    private String verdictClass;
    private String domain;
    private String topic;

    public ArticleDto() {
    }

    public ArticleDto(String id, String title, String source, String url, String publishedAt,
                      String description, String image, int score, String verdict,
                      String verdictClass, String domain, String topic) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.url = url;
        this.publishedAt = publishedAt;
        this.description = description;
        this.image = image;
        this.score = score;
        this.verdict = verdict;
        this.verdictClass = verdictClass;
        this.domain = domain;
        this.topic = topic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
