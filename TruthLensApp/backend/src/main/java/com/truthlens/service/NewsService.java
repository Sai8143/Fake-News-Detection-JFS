package com.truthlens.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truthlens.dto.ArticleDto;
import com.truthlens.dto.NewsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NewsService {

    private final RestTemplate restTemplate;
    private final TruthLensAnalysisService analysisService;
    private final ObjectMapper objectMapper;

    public NewsService(RestTemplate restTemplate,
                       TruthLensAnalysisService analysisService,
                       ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.analysisService = analysisService;
        this.objectMapper = objectMapper;
    }

    public NewsResponse getNewsFeed(List<String> topics, String apiKey, int pageSize) {
        List<ArticleDto> articles = new ArrayList<>();
        List<String> activeTopics = (topics != null && !topics.isEmpty()) ? topics : List.of("technology");
        int limit = pageSize > 0 ? pageSize : 10;

        if (apiKey != null && !apiKey.trim().isEmpty()) {
            for (String topic : activeTopics.subList(0, Math.min(2, activeTopics.size()))) {
                try {
                    String url = UriComponentsBuilder.fromHttpUrl("https://gnews.io/api/v4/top-headlines")
                            .queryParam("topic", topic.trim())
                            .queryParam("lang", "en")
                            .queryParam("max", limit)
                            .queryParam("apikey", apiKey.trim())
                            .toUriString();

                    String rawJson = restTemplate.getForObject(url, String.class);
                    if (rawJson != null) {
                        JsonNode root = objectMapper.readTree(rawJson);
                        JsonNode articlesNode = root.get("articles");
                        if (articlesNode != null && articlesNode.isArray()) {
                            for (JsonNode item : articlesNode) {
                                ArticleDto article = mapJsonNodeToArticle(item, topic.trim());
                                articles.add(article);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("GNews feed error for topic " + topic + ": " + e.getMessage());
                }
            }
        }

        // If no articles returned (no key, invalid key, or network issue), use sample dataset
        if (articles.isEmpty()) {
            articles = getSampleArticles(activeTopics);
        }

        // Sort by score descending (credible first)
        articles.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        return new NewsResponse(articles, articles.size());
    }

    public NewsResponse searchNews(String query, String apiKey) {
        if (query == null || query.trim().isEmpty()) {
            return new NewsResponse(Collections.emptyList(), 0);
        }

        List<ArticleDto> articles = new ArrayList<>();

        if (apiKey != null && !apiKey.trim().isEmpty()) {
            try {
                String url = UriComponentsBuilder.fromHttpUrl("https://gnews.io/api/v4/search")
                        .queryParam("q", query.trim())
                        .queryParam("lang", "en")
                        .queryParam("max", 10)
                        .queryParam("apikey", apiKey.trim())
                        .toUriString();

                String rawJson = restTemplate.getForObject(url, String.class);
                if (rawJson != null) {
                    JsonNode root = objectMapper.readTree(rawJson);
                    JsonNode articlesNode = root.get("articles");
                    if (articlesNode != null && articlesNode.isArray()) {
                        for (JsonNode item : articlesNode) {
                            ArticleDto article = mapJsonNodeToArticle(item, "search");
                            articles.add(article);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("GNews search error for query '" + query + "': " + e.getMessage());
            }
        }

        if (articles.isEmpty()) {
            articles = searchSampleArticles(query);
        }

        articles.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return new NewsResponse(articles, articles.size());
    }

    private ArticleDto mapJsonNodeToArticle(JsonNode item, String topic) {
        String url = item.has("url") ? item.get("url").asText("") : "";
        String title = item.has("title") ? item.get("title").asText("") : "";
        String description = item.has("description") ? item.get("description").asText("") : "";
        String image = item.has("image") ? item.get("image").asText("") : "";
        String publishedAt = item.has("publishedAt") ? item.get("publishedAt").asText("") : "";
        String domain = analysisService.extractDomain(url);

        String sourceName = domain;
        if (item.has("source") && item.get("source").has("name")) {
            sourceName = item.get("source").get("name").asText(domain);
        }

        TruthLensAnalysisService.HeuristicResult hr = analysisService.evaluateHeuristics(title + " " + description, url, title);
        String[] verdict = analysisService.generateVerdict(hr.score);

        return new ArticleDto(
                generateHashId(url),
                title,
                sourceName,
                url,
                publishedAt,
                description,
                image,
                hr.score,
                verdict[0],
                verdict[1],
                domain,
                topic
        );
    }

    private String generateHashId(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return String.format("%032x", new BigInteger(1, digest)).substring(0, 8);
        } catch (Exception e) {
            return UUID.randomUUID().toString().substring(0, 8);
        }
    }

    private List<ArticleDto> getSampleArticles(List<String> topics) {
        List<ArticleDto> samples = new ArrayList<>();
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        for (String topic : topics) {
            switch (topic.toLowerCase()) {
                case "science":
                    samples.add(buildArticle("NASA Webb Space Telescope Discovers Atmospheric Water on Earth-Sized Exoplanet",
                            "https://science.org/news/webb-exoplanet-discovery", "Science Magazine",
                            "Astronomers using the James Webb Space Telescope report atmospheric water signatures according to peer-reviewed data.",
                            "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600", "science"));
                    samples.add(buildArticle("BREAKING: Scientists Wake Up Dinosaurs In Secret Underground Lab!!",
                            "https://beforeitsnews.com/shocking-dino-cloning", "Before It's News",
                            "You won't believe what hidden truth was uncovered in this underground military bunker.",
                            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600", "science"));
                    break;
                case "health":
                    samples.add(buildArticle("World Health Organization Releases Updated Global Dietary Guidelines",
                            "https://reuters.com/health/who-dietary-guidance", "Reuters",
                            "The World Health Organization published comprehensive guidance on micronutrients and cardiovascular health based on extensive clinical trials.",
                            "https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=600", "health"));
                    samples.add(buildArticle("SHOCKING: One Secret Fruit Cures Every Illness and Big Pharma is Censoring It!",
                            "https://naturalnews.com/miracle-cure-censored", "Natural News",
                            "Doctors are furious because this ancient secret exposed will destroy modern hospitals.",
                            "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600", "health"));
                    break;
                case "politics":
                    samples.add(buildArticle("Senate Committee Approves Bipartisan Artificial Intelligence Safety Framework",
                            "https://apnews.com/article/ai-safety-legislation-senate", "Associated Press",
                            "Lawmakers from both parties reached consensus on federal standards for foundation model watermarking and transparency.",
                            "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=600", "politics"));
                    break;
                case "sports":
                    samples.add(buildArticle("Olympic Committee Announces Expanded Urban Sports Roster for Upcoming Games",
                            "https://bbc.com/sport/olympics-urban-sports", "BBC Sport",
                            "Officials confirmed the addition of new youth-focused athletic events following successful test tournaments.",
                            "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=600", "sports"));
                    break;
                case "technology":
                default:
                    samples.add(buildArticle("Quantum Computing Breakthrough: Researchers Achieve Fault-Tolerant Logical Qubits",
                            "https://nature.com/articles/quantum-breakthrough-2026", "Nature",
                            "A research team led by scientists demonstrated error suppression across multi-qubit physical clusters.",
                            "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=600", "technology"));
                    samples.add(buildArticle("Open Source Consortium Launches Universal Microchip Architecture",
                            "https://theguardian.com/technology/chips-consortium", "The Guardian",
                            "Leading tech universities and open-source foundations collaborate on royalty-free computing chips.",
                            "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600", "technology"));
                    samples.add(buildArticle("YOU WON'T BELIEVE: Phones secretly transmitting private dreams to Deep State!",
                            "https://infowars.com/phones-secret-brainwave-hack", "Infowars",
                            "Urgent warning: Bombshell secret exposed! They don't want you to see this wake up call.",
                            "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600", "technology"));
                    break;
            }
        }

        // Add a prompt article if GNews key is not set
        samples.add(0, new ArticleDto(
                "setup-key",
                "Tip: Add your free GNews API key in Profile to fetch live worldwide news",
                "TruthLens System",
                "https://gnews.io",
                now,
                "Sign up at gnews.io for a free API key (100 requests/day). Add it in the Profile tab to unlock real-time headlines.",
                "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=600",
                92,
                "CREDIBLE",
                "real",
                "gnews.io",
                "system"
        ));

        return samples;
    }

    private List<ArticleDto> searchSampleArticles(String query) {
        String q = query.toLowerCase();
        List<ArticleDto> all = getSampleArticles(List.of("technology", "science", "health", "politics", "sports"));
        List<ArticleDto> matched = new ArrayList<>();
        for (ArticleDto a : all) {
            if (a.getTitle().toLowerCase().contains(q) || a.getDescription().toLowerCase().contains(q)) {
                matched.add(a);
            }
        }
        if (matched.isEmpty()) {
            // Generate a synthetic analyzed search result for the query
            matched.add(buildArticle(
                    "Latest Developments and Analysis on: " + query,
                    "https://reuters.com/search?q=" + query.replaceAll("\\s+", "+"),
                    "Reuters News Wire",
                    "Independent reporting and factual updates regarding " + query + " as verified by researchers and correspondents.",
                    "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=600",
                    "search"
            ));
        }
        return matched;
    }

    private ArticleDto buildArticle(String title, String url, String source, String description, String image, String topic) {
        String domain = analysisService.extractDomain(url);
        TruthLensAnalysisService.HeuristicResult hr = analysisService.evaluateHeuristics(title + " " + description, url, title);
        String[] verdict = analysisService.generateVerdict(hr.score);

        return new ArticleDto(
                generateHashId(url),
                title,
                source,
                url,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                description,
                image,
                hr.score,
                verdict[0],
                verdict[1],
                domain,
                topic
        );
    }
}
