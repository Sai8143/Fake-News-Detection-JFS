package com.truthlens.controller;

import com.truthlens.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse(
                "ok",
                true,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "Spring Boot 3.2.5",
                System.getProperty("java.version")
        ));
    }
}
