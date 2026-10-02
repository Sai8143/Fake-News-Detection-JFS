package com.truthlens.dto;

import java.util.List;

public class BatchAnalyzeRequest {
    private List<AnalyzeRequest> articles;

    public BatchAnalyzeRequest() {
    }

    public BatchAnalyzeRequest(List<AnalyzeRequest> articles) {
        this.articles = articles;
    }

    public List<AnalyzeRequest> getArticles() {
        return articles;
    }

    public void setArticles(List<AnalyzeRequest> articles) {
        this.articles = articles;
    }
}
