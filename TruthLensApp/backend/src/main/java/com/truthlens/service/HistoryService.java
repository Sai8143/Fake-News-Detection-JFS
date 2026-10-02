package com.truthlens.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthlens.dto.AnalyzeResponse;
import com.truthlens.dto.SignalDto;
import com.truthlens.entity.AnalysisRecord;
import com.truthlens.repository.AnalysisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final AnalysisRepository analysisRepository;
    private final ObjectMapper objectMapper;

    public HistoryService(AnalysisRepository analysisRepository, ObjectMapper objectMapper) {
        this.analysisRepository = analysisRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<AnalyzeResponse> getRecentHistory(int limit) {
        List<AnalysisRecord> records = analysisRepository.findAllByOrderByCreatedAtDesc();
        if (limit > 0 && records.size() > limit) {
            records = records.subList(0, limit);
        }
        return records.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteHistoryItem(Long id) {
        if (analysisRepository.existsById(id)) {
            analysisRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void clearAllHistory() {
        analysisRepository.deleteAll();
    }

    private AnalyzeResponse toResponse(AnalysisRecord record) {
        List<SignalDto> signals = new ArrayList<>();
        if (record.getSignalsJson() != null && !record.getSignalsJson().isEmpty()) {
            try {
                signals = objectMapper.readValue(record.getSignalsJson(), new TypeReference<List<SignalDto>>() {});
            } catch (Exception ignored) {
            }
        }

        String ts = record.getCreatedAt() != null ?
                record.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME) : "";

        return new AnalyzeResponse(
                record.getId(),
                record.getVerdict(),
                record.getVerdictClass(),
                record.getScore(),
                record.getHeuristicScore(),
                record.getMlScore(),
                signals,
                record.getSummary(),
                record.getDomain(),
                ts
        );
    }
}
