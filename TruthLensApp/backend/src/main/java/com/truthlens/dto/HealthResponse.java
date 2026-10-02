package com.truthlens.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthResponse {
    private String status;

    @JsonProperty("model_loaded")
    private boolean modelLoaded;

    private String timestamp;
    private String framework;
    private String javaVersion;

    public HealthResponse() {
    }

    public HealthResponse(String status, boolean modelLoaded, String timestamp, String framework, String javaVersion) {
        this.status = status;
        this.modelLoaded = modelLoaded;
        this.timestamp = timestamp;
        this.framework = framework;
        this.javaVersion = javaVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isModelLoaded() {
        return modelLoaded;
    }

    public void setModelLoaded(boolean modelLoaded) {
        this.modelLoaded = modelLoaded;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }
}
