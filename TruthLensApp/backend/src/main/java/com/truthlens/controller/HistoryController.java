package com.truthlens.controller;

import com.truthlens.dto.AnalyzeResponse;
import com.truthlens.service.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<List<AnalyzeResponse>> getHistory(
            @RequestParam(name = "limit", defaultValue = "50") int limit) {
        List<AnalyzeResponse> list = historyService.getRecentHistory(limit);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteHistoryItem(@PathVariable Long id) {
        boolean deleted = historyService.deleteHistoryItem(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("status", "deleted", "id", id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearHistory() {
        historyService.clearAllHistory();
        return ResponseEntity.ok(Map.of("status", "cleared"));
    }
}
