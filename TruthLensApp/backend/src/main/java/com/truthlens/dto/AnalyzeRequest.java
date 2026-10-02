package com.truthlens.dto;

public class AnalyzeRequest {
    private String url = "";
    private String title = "";
    private String text = "";
    private String uid = "default";

    public AnalyzeRequest() {
    }

    public AnalyzeRequest(String url, String title, String text) {
        this.url = url != null ? url : "";
        this.title = title != null ? title : "";
        this.text = text != null ? text : "";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url != null ? url : "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title : "";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text != null ? text : "";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid != null ? uid : "default";
    }
}
