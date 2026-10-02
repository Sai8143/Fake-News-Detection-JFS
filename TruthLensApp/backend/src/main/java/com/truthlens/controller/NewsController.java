package com.truthlens.controller;

import com.truthlens.dto.NewsResponse;
import com.truthlens.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/feed")
    public ResponseEntity<NewsResponse> getFeed(
            @RequestParam(name = "topics", defaultValue = "technology") String topics,
            @RequestParam(name = "apikey", defaultValue = "") String apiKey,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

        List<String> topicList = Arrays.stream(topics.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        NewsResponse response = newsService.getNewsFeed(topicList, apiKey, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<NewsResponse> search(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "apikey", defaultValue = "") String apiKey) {

        NewsResponse response = newsService.searchNews(query, apiKey);
        return ResponseEntity.ok(response);
    }
}
