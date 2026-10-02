package com.truthlens.dto;

public class SignalDto {
    private String type;   // "good", "bad", "neutral"
    private String label;

    public SignalDto() {
    }

    public SignalDto(String type, String label) {
        this.type = type;
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
