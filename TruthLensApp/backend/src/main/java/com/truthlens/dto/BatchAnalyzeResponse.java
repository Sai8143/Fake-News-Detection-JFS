package com.truthlens.dto;

import java.util.List;

public class BatchAnalyzeResponse {
    private List<BatchArticleResult> results;

    public BatchAnalyzeResponse() {
    }

    public BatchAnalyzeResponse(List<BatchArticleResult> results) {
        this.results = results;
    }

    public List<BatchArticleResult> getResults() {
        return results;
    }

    public void setResults(List<BatchArticleResult> results) {
        this.results = results;
    }
}
