package com.truthlens.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String indexHtml() {
        return "forward:/index.html";
    }

    @GetMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiHome() {
        return ResponseEntity.ok(Map.of(
                "message", "TruthLens Spring Boot JFS Backend API is running 🚀",
                "status", "success",
                "framework", "Spring Boot 3.2.5 (Java 21)",
                "database", "H2 Embedded Persistent Database",
                "endpoints", List.of(
                        "/api/health",
                        "/api/analyze",
                        "/api/batch",
                        "/api/news/feed",
                        "/api/news/search",
                        "/api/profile",
                        "/api/history",
                        "/h2-console"
                )
        ));
    }
}
