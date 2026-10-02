package com.truthlens;

import com.truthlens.dto.AnalyzeRequest;
import com.truthlens.dto.AnalyzeResponse;
import com.truthlens.service.TruthLensAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TruthLensApplicationTests {

    @Autowired
    private TruthLensAnalysisService analysisService;

    @Test
    void contextLoads() {
        assertNotNull(analysisService);
    }

    @Test
    void testCredibleAnalysis() {
        AnalyzeRequest request = new AnalyzeRequest(
                "https://bbc.com/news/world-12345",
                "Scientists report major climate findings according to peer-reviewed data",
                "By Alice Smith, science reporter. In an extensive peer-reviewed study, researchers documented findings over several years of observation."
        );

        AnalyzeResponse response = analysisService.analyze(request);
        assertNotNull(response);
        assertEquals("CREDIBLE", response.getVerdict());
        assertEquals("real", response.getVerdictClass());
        assertTrue(response.getScore() >= 65);
    }

    @Test
    void testFakeNewsAnalysis() {
        AnalyzeRequest request = new AnalyzeRequest(
                "http://infowars.com/banned-secret",
                "SHOCKING UNBELIEVABLE BOMBSHELL: YOU WON'T BELIEVE WHAT THEY CENSORED",
                "Urgent wake up! Deep state secret exposed! They don't want you to see this outrageous lie."
        );

        AnalyzeResponse response = analysisService.analyze(request);
        assertNotNull(response);
        assertEquals("LIKELY FAKE", response.getVerdict());
        assertEquals("fake", response.getVerdictClass());
        assertTrue(response.getScore() < 35);
    }
}
