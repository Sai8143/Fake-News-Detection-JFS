package com.truthlens.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthlens.dto.AnalyzeRequest;
import com.truthlens.dto.AnalyzeResponse;
import com.truthlens.dto.SignalDto;
import com.truthlens.entity.AnalysisRecord;
import com.truthlens.repository.AnalysisRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TruthLensAnalysisService {

    private final AnalysisRepository analysisRepository;
    private final ProfileService profileService;
    private final ObjectMapper objectMapper;

    public static final Set<String> TRUSTED_DOMAINS = Set.of(
            "bbc.com", "bbc.co.uk", "reuters.com", "apnews.com", "npr.org",
            "theguardian.com", "nytimes.com", "washingtonpost.com", "economist.com",
            "nature.com", "science.org", "scientificamerican.com", "time.com",
            "theatlantic.com", "politifact.com", "snopes.com", "factcheck.org",
            "bloomberg.com", "ft.com", "wsj.com", "pbs.org", "propublica.org",
            "thehindu.com", "ndtv.com", "indianexpress.com", "livemint.com"
    );

    public static final Set<String> UNTRUSTED_DOMAINS = Set.of(
            "infowars.com", "naturalnews.com", "beforeitsnews.com",
            "worldnewsdailyreport.com", "empirenews.net", "nationalreport.net",
            "huzlers.com", "anonews.co", "newspunch.com"
    );

    private static final List<Pattern> CLICKBAIT_PATTERNS = List.of(
            Pattern.compile("\\bshocking\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bunbelievable\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("you won't believe", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bsecret\\b.*\\bexposed\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bbombshell\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\burgent\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bbanned\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bcensored\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("they don't want you", Pattern.CASE_INSENSITIVE),
            Pattern.compile("hidden truth", Pattern.CASE_INSENSITIVE),
            Pattern.compile("wake up", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bdeep state\\b", Pattern.CASE_INSENSITIVE)
    );

    private static final Pattern AUTHOR_PATTERN = Pattern.compile("\\bby\\s+[A-Z][a-z]+|author:|reporter:", Pattern.CASE_INSENSITIVE);
    private static final Pattern CITATIONS_PATTERN = Pattern.compile("according to|study shows|research|data shows|scientists|experts say", Pattern.CASE_INSENSITIVE);
    private static final List<String> EMOTIONAL_WORDS = List.of("outrage", "infuriating", "disgusting", "horrifying", "they're lying");

    public TruthLensAnalysisService(AnalysisRepository analysisRepository,
                                    ProfileService profileService,
                                    ObjectMapper objectMapper) {
        this.analysisRepository = analysisRepository;
        this.profileService = profileService;
        this.objectMapper = objectMapper;
    }

    public static class HeuristicResult {
        public final int score;
        public final List<SignalDto> signals;

        public HeuristicResult(int score, List<SignalDto> signals) {
            this.score = score;
            this.signals = signals;
        }
    }

    public String extractDomain(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }
        try {
            String sanitized = url.trim();
            if (!sanitized.startsWith("http://") && !sanitized.startsWith("https://")) {
                sanitized = "https://" + sanitized;
            }
            URI uri = new URI(sanitized);
            String host = uri.getHost();
            if (host == null) {
                return "";
            }
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (Exception e) {
            return "";
        }
    }

    public HeuristicResult evaluateHeuristics(String text, String url, String title) {
        int score = 50;
        List<SignalDto> signals = new ArrayList<>();
        String safeText = text != null ? text : "";
        String safeTitle = title != null ? title : "";
        String safeUrl = url != null ? url.trim() : "";
        String content = (safeTitle + " " + safeText).toLowerCase();
        String domain = extractDomain(safeUrl).toLowerCase();

        // 1. Domain Reputation
        if (!domain.isEmpty()) {
            boolean isTrusted = TRUSTED_DOMAINS.stream().anyMatch(domain::contains);
            boolean isUntrusted = UNTRUSTED_DOMAINS.stream().anyMatch(domain::contains);

            if (isTrusted) {
                score += 30;
                signals.add(new SignalDto("good", "Trusted source (" + domain + ")"));
            } else if (isUntrusted) {
                score -= 40;
                signals.add(new SignalDto("bad", "Known misinformation site (" + domain + ")"));
            }
        }

        // 2. HTTPS Protocol
        if (safeUrl.toLowerCase().startsWith("https://")) {
            score += 5;
            signals.add(new SignalDto("good", "Secure HTTPS"));
        }

        // 3. Clickbait Pattern Detection
        int clickbaitCount = 0;
        for (Pattern p : CLICKBAIT_PATTERNS) {
            if (p.matcher(content).find()) {
                clickbaitCount++;
            }
        }
        if (clickbaitCount >= 3) {
            score -= 20;
            signals.add(new SignalDto("bad", clickbaitCount + " clickbait patterns detected"));
        } else if (clickbaitCount == 0) {
            score += 8;
            signals.add(new SignalDto("good", "No clickbait detected"));
        }

        // 4. Excessive Capitalization in Title
        String[] words = safeTitle.trim().split("\\s+");
        if (words.length > 0 && !safeTitle.trim().isEmpty()) {
            long capsWords = Arrays.stream(words)
                    .filter(w -> w.length() > 2 && w.equals(w.toUpperCase()) && w.matches(".*[A-Z].*"))
                    .count();
            double capsRatio = (double) capsWords / words.length;
            if (capsRatio > 0.4) {
                score -= 15;
                signals.add(new SignalDto("bad", "Excessive capitalization in headline"));
            }
        }

        // 5. Author Attribution
        if (AUTHOR_PATTERN.matcher(safeText).find()) {
            score += 8;
            signals.add(new SignalDto("good", "Author attributed"));
        } else {
            signals.add(new SignalDto("neutral", "No clear author"));
        }

        // 6. Citations and Research Indicators
        if (CITATIONS_PATTERN.matcher(content).find()) {
            score += 10;
            signals.add(new SignalDto("good", "Cites sources / research"));
        }

        // 7. Emotional Manipulation Language
        boolean hasEmotional = EMOTIONAL_WORDS.stream().anyMatch(content::contains);
        if (hasEmotional) {
            score -= 12;
            signals.add(new SignalDto("bad", "Emotional manipulation language detected"));
        }

        // 8. Content Depth / Word Count
        int wordCount = safeText.trim().isEmpty() ? 0 : safeText.trim().split("\\s+").length;
        if (wordCount > 400) {
            score += 8;
            signals.add(new SignalDto("good", "In-depth article (" + wordCount + " words)"));
        } else if (wordCount > 0 && wordCount < 50) {
            score -= 5;
            signals.add(new SignalDto("neutral", "Very short content snippet"));
        }

        int finalScore = Math.max(0, Math.min(100, score));
        return new HeuristicResult(finalScore, signals);
    }

    public String[] generateVerdict(int score) {
        if (score >= 65) {
            return new String[]{"CREDIBLE", "real"};
        } else if (score >= 35) {
            return new String[]{"MIXED", "mixed"};
        } else {
            return new String[]{"LIKELY FAKE", "fake"};
        }
    }

    public String generateSummary(int score, String domain, List<SignalDto> signals) {
        String sourceLabel = (domain != null && !domain.isEmpty()) ? "'" + domain + "'" : "the source";
        if (score >= 65) {
            return "This article appears credible. Positive trust signals detected from " + sourceLabel + ".";
        } else if (score >= 35) {
            return "Mixed credibility signals from " + sourceLabel + ". Verify through multiple trusted sources.";
        } else {
            List<String> badSignals = signals.stream()
                    .filter(s -> "bad".equals(s.getType()))
                    .map(SignalDto::getLabel)
                    .limit(3)
                    .collect(Collectors.toList());
            String redFlags = badSignals.isEmpty() ? "Low trust metrics" : String.join(", ", badSignals);
            return "Red flags detected: " + redFlags + ". Exercise caution before sharing.";
        }
    }

    public AnalyzeResponse analyze(AnalyzeRequest request) {
        String url = request.getUrl() != null ? request.getUrl().trim() : "";
        String title = request.getTitle() != null ? request.getTitle().trim() : "";
        String text = request.getText() != null ? request.getText().trim() : "";
        String uid = (request.getUid() != null && !request.getUid().trim().isEmpty()) ? request.getUid() : "default";

        String combinedText = (title + " " + text).trim();
        String domain = extractDomain(url);

        HeuristicResult heuristic = evaluateHeuristics(combinedText, url, title);
        int finalScore = heuristic.score;
        String[] verdictPair = generateVerdict(finalScore);
        String verdict = verdictPair[0];
        String verdictClass = verdictPair[1];

        String summary = generateSummary(finalScore, domain, heuristic.signals);

        // Limit signals to top 6
        List<SignalDto> trimmedSignals = heuristic.signals.stream().limit(6).collect(Collectors.toList());
        String signalsJson = "[]";
        try {
            signalsJson = objectMapper.writeValueAsString(trimmedSignals);
        } catch (Exception ignored) {
        }

        // Persist to Database
        AnalysisRecord record = new AnalysisRecord(
                url, title, text, verdict, verdictClass, finalScore, heuristic.score,
                null, domain, summary, signalsJson
        );
        record = analysisRepository.save(record);

        // Update User Profile stats
        profileService.incrementStats(uid, "fake".equalsIgnoreCase(verdictClass));

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        return new AnalyzeResponse(
                record.getId(),
                verdict,
                verdictClass,
                finalScore,
                heuristic.score,
                null,
                trimmedSignals,
                summary,
                domain,
                timestamp
        );
    }
}
