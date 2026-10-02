package com.truthlens.dto;

import java.util.List;

public class NewsResponse {
    private List<ArticleDto> articles;
    private int count;

    public NewsResponse() {
    }

    public NewsResponse(List<ArticleDto> articles, int count) {
        this.articles = articles;
        this.count = count;
    }

    public List<ArticleDto> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleDto> articles) {
        this.articles = articles;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
