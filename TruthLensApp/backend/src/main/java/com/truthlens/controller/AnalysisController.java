package com.truthlens.controller;

import com.truthlens.dto.*;
import com.truthlens.service.TruthLensAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalysisController {

    private final TruthLensAnalysisService analysisService;

    public AnalysisController(TruthLensAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody(required = false) AnalyzeRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No data provided"));
        }

        String url = request.getUrl() != null ? request.getUrl().trim() : "";
        String title = request.getTitle() != null ? request.getTitle().trim() : "";
        String text = request.getText() != null ? request.getText().trim() : "";

        if (url.isEmpty() && title.isEmpty() && text.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Provide url, title, or text"));
        }

        AnalyzeResponse response = analysisService.analyze(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchAnalyzeResponse> batchAnalyze(@RequestBody(required = false) BatchAnalyzeRequest request) {
        if (request == null || request.getArticles() == null || request.getArticles().isEmpty()) {
            return ResponseEntity.ok(new BatchAnalyzeResponse(List.of()));
        }

        List<BatchArticleResult> results = new ArrayList<>();
        List<AnalyzeRequest> articles = request.getArticles();
        int max = Math.min(20, articles.size());

        for (int i = 0; i < max; i++) {
            AnalyzeRequest item = articles.get(i);
            String url = item.getUrl() != null ? item.getUrl() : "";
            String title = item.getTitle() != null ? item.getTitle() : "";
            String text = item.getText() != null ? item.getText() : "";
            String domain = analysisService.extractDomain(url);

            TruthLensAnalysisService.HeuristicResult hr = analysisService.evaluateHeuristics(title + " " + text, url, title);
            String[] verdict = analysisService.generateVerdict(hr.score);

            results.add(new BatchArticleResult(title, url, domain, hr.score, verdict[0], verdict[1]));
        }

        return ResponseEntity.ok(new BatchAnalyzeResponse(results));
    }
}
