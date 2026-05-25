package habitTracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import habitTracker.business.HabitTrackerLog;
import habitTracker.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeepSeekService {

    @Value("${deepseek.api.key}")
    private String deepSeekApiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String deepSeekApiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AiInsightResponse generateInsights(String username, List<HabitListResponse> habits, List<HabitTrackerLog> recentLogs) {
        String userPrompt = buildInsightPrompt(username, habits, recentLogs);
        String systemPrompt = buildInsightSystemPrompt(habits);
        String raw = callDeepSeek(userPrompt, systemPrompt, 0.7);

        AiInsightResponse response = new AiInsightResponse();
        response.setGeneratedAt(System.currentTimeMillis());

        try {
            String cleaned = raw.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").trim();
            DeepSeekInsightResponse deepSeekInsightResponse = objectMapper.readValue(cleaned, DeepSeekInsightResponse.class);

            if (deepSeekInsightResponse.getInsights() != null) {
                response.setInsightText(deepSeekInsightResponse.getInsights());
            }

            List<AiRecommendation> videos = parseVideos(deepSeekInsightResponse.getRecommendations());
            List<AiRecommendation> books = parseBooks(deepSeekInsightResponse.getRecommendations());

            List<AiRecommendation> all = new ArrayList<>();
            all.addAll(videos);
            all.addAll(books);
            response.setRecommendations(all);

        } catch (Exception e) {
            response.setInsightText("Unable to load insights right now. Please try again.");
            response.setRecommendations(new ArrayList<>());
        }

        return response;
    }

    public AiChatResponse chat(String username, String userMessage, List<HabitListResponse> habits, List<HabitTrackerLog> recentLogs, List<String> conversationHistory) {
        String systemPrompt = buildChatSystemPrompt(username, habits, recentLogs);
        String raw = callDeepSeekWithHistory(userMessage, systemPrompt, conversationHistory, 1.5);

        AiChatResponse response = new AiChatResponse();
        response.setTimestamp(System.currentTimeMillis());

        try {
            String cleaned = raw.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").trim();
            DeepSeekChatResponse deepSeekChatResponse = objectMapper.readValue(cleaned, DeepSeekChatResponse.class);

            if (deepSeekChatResponse.getReply() != null) {
                response.setReply(deepSeekChatResponse.getReply());
            }

            List<AiRecommendation> videos = parseVideos(deepSeekChatResponse.getRecommendations());
            List<AiRecommendation> books = parseBooks(deepSeekChatResponse.getRecommendations());

            List<AiRecommendation> all = new ArrayList<>();
            all.addAll(videos);
            all.addAll(books);
            response.setRecommendations(all);

        } catch (Exception e) {
            response.setReply("I couldn't process your message right now. Please try again.");
            response.setRecommendations(new ArrayList<>());
        }

        return response;
    }

    private List<AiRecommendation> parseVideos(List<DeepSeekRecommendation> recs) {
        List<AiRecommendation> videos = new ArrayList<>();
        if (recs == null) return videos;

        for (DeepSeekRecommendation r : recs) {
            if (!"VIDEO".equalsIgnoreCase(r.getType())) continue;
            if (r.getTitle() == null || r.getTitle().isBlank()) continue;
            String searchQuery = URLEncoder.encode(r.getTitle(), StandardCharsets.UTF_8);
            AiRecommendation rec = new AiRecommendation();
            rec.setType("VIDEO");
            rec.setTitle(r.getTitle().trim());
            if (r.getDescription() != null) {
                rec.setDescription(r.getDescription().trim());
            }
            rec.setUrl("https://www.youtube.com/results?search_query=" + searchQuery);
            videos.add(rec);
        }

        return videos;
    }

    private List<AiRecommendation> parseBooks(List<DeepSeekRecommendation> recs) {
        List<AiRecommendation> books = new ArrayList<>();
        if (recs == null) return books;

        for (DeepSeekRecommendation r : recs) {
            if (!"BOOK".equalsIgnoreCase(r.getType())) continue;
            if (r.getTitle() == null || r.getTitle().isBlank()) continue;
            String author = "";
            if (r.getAuthor() != null) {
                author = r.getAuthor().trim();
            }
            String searchQuery = URLEncoder.encode(r.getTitle().trim() + " " + author, StandardCharsets.UTF_8);
            AiRecommendation rec = new AiRecommendation();
            rec.setType("BOOK");
            rec.setTitle(r.getTitle().trim());
            rec.setAuthor(author);
            if (r.getDescription() != null) {
                rec.setDescription(r.getDescription().trim());
            }
            rec.setUrl("https://www.goodreads.com/search?q=" + searchQuery);
            books.add(rec);
        }

        return books;
    }

    private String callDeepSeek(String userPrompt, String systemPrompt, double temperature) {
        try {
            List<DeepSeekMessage> messages = new ArrayList<>();
            messages.add(new DeepSeekMessage("system", systemPrompt));
            messages.add(new DeepSeekMessage("user", userPrompt));

            DeepSeekRequest requestBody = new DeepSeekRequest();
            requestBody.setModel("deepseek-chat");
            requestBody.setTemperature(temperature);
            requestBody.setMessages(messages);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(deepSeekApiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + deepSeekApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .build();

            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            DeepSeekApiResponse apiResponse = objectMapper.readValue(resp.body(), DeepSeekApiResponse.class);
            return apiResponse.getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            return "{\"insights\":\"Error calling AI service.\",\"recommendations\":[]}";
        }
    }

    private String callDeepSeekWithHistory(String userMessage, String systemPrompt, List<String> history, double temperature) {
        try {
            List<DeepSeekMessage> messages = new ArrayList<>();
            messages.add(new DeepSeekMessage("system", systemPrompt));

            if (history != null) {
                for (int i = 0; i < history.size(); i++) {
                    if (i % 2 == 0) {
                        messages.add(new DeepSeekMessage("user", history.get(i)));
                    } else {
                        messages.add(new DeepSeekMessage("assistant", history.get(i)));
                    }
                }
            }

            messages.add(new DeepSeekMessage("user", userMessage));

            DeepSeekRequest requestBody = new DeepSeekRequest();
            requestBody.setModel("deepseek-chat");
            requestBody.setTemperature(temperature);
            requestBody.setMessages(messages);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(deepSeekApiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + deepSeekApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .build();

            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            DeepSeekApiResponse apiResponse = objectMapper.readValue(resp.body(), DeepSeekApiResponse.class);
            return apiResponse.getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            return "{\"reply\":\"Error. Please try again.\",\"recommendations\":[]}";
        }
    }

    private String buildInsightSystemPrompt(List<HabitListResponse> habits) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert personal habit coach AI. ");
        sb.append("Respond ONLY with valid JSON, no markdown fences, in this exact structure: ");
        sb.append("{ \"insights\": \"<two to three sentences of rich personalised feedback referencing the user's specific habits, streaks, and recent activity logs>\", ");
        sb.append("\"recommendations\": [ ");
        sb.append("{ \"type\": \"VIDEO\", \"title\": \"<specific YouTube search title relevant to one of the user's habits>\", \"description\": \"<one short sentence on why this is relevant>\" }, ");
        sb.append("{ \"type\": \"VIDEO\", \"title\": \"...\", \"description\": \"...\" }, ");
        sb.append("{ \"type\": \"BOOK\", \"title\": \"<real published book title>\", \"author\": \"<real author name>\", \"description\": \"<one sentence on why this suits the user's habits>\", \"url\": \"<ignored>\" }, ");
        sb.append("{ \"type\": \"BOOK\", \"title\": \"...\", \"author\": \"...\", \"description\": \"...\", \"url\": \"...\" } ");
        sb.append("] } ");
        sb.append("RULES: ");
        sb.append("1. Return exactly 2 VIDEO items and exactly 2 BOOK items. ");
        sb.append("2. Do NOT include a url field on VIDEO items. ");
        sb.append("3. Books must be real published books. ");
        sb.append("4. Tailor everything to the user's actual habit types. ");
        sb.append("5. Vary recommendations each time. ");
        sb.append("6. Books must be niche and specific to the user's activity types. Avoid mainstream self-help bestsellers. ");
        sb.append("7. Write insights as natural flowing sentences with no semicolons, colons, dashes, or bullet points.");

        if (habits != null && !habits.isEmpty()) {
            sb.append(" The user's habits are: ");
            for (int i = 0; i < habits.size(); i++) {
                sb.append(habits.get(i).getActivityName());
                if (i < habits.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(".");
        }

        return sb.toString();
    }

    private String buildChatSystemPrompt(String username, List<HabitListResponse> habits, List<HabitTrackerLog> recentLogs) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a warm, encouraging personal AI habit coach for ").append(username).append(". Their current habits:\n");

        if (habits != null) {
            for (HabitListResponse h : habits) {
                sb.append("- ").append(h.getActivityName())
                        .append(": ").append(h.getDescription())
                        .append(", current streak=").append(h.getCurrentStreak()).append(" days")
                        .append(", longest streak=").append(h.getLongestStreak()).append(" days")
                        .append(", frequency=").append(h.getFrequency()).append("x per week");
                if (h.getLastCompletedDate() != null) {
                    sb.append(", last completed=").append(h.getLastCompletedDate());
                }
                sb.append("\n");
            }
        }

        if (recentLogs != null && !recentLogs.isEmpty()) {
            sb.append("\nRecent activity logs (last 10):\n");
            for (HabitTrackerLog log : recentLogs) {
                sb.append("- Date: ").append(log.getDate_of_activity());
                if (log.getDuration_minutes() > 0) {
                    sb.append(", duration=").append(log.getDuration_minutes()).append(" mins");
                }
                if (log.getDistance_km() > 0) {
                    sb.append(", distance=").append(log.getDistance_km()).append(" km");
                }
                if (log.getCalories_burned() > 0) {
                    sb.append(", calories=").append(log.getCalories_burned());
                }
                if (log.getNotes() != null && !log.getNotes().isBlank()) {
                    sb.append(", notes=").append(log.getNotes());
                }
                sb.append("\n");
            }
        }

        sb.append("\nRespond ONLY with valid JSON, no markdown fences: ");
        sb.append("{ \"reply\": \"<warm specific encouraging response referencing their actual habits and recent logs>\", \"recommendations\": [] } ");
        sb.append("Populate recommendations only when the user explicitly asks for book or video suggestions. ");
        sb.append("For VIDEO items use: { \"type\": \"VIDEO\", \"title\": \"<specific video search title>\", \"description\": \"<one short sentence on why this is relevant>\" } ");
        sb.append("Do NOT include a url field on VIDEO items. ");
        sb.append("For BOOK items use: { \"type\": \"BOOK\", \"title\": \"<real title>\", \"author\": \"<real author>\", \"description\": \"<relevance to their habits>\", \"url\": \"<ignored>\" }");

        return sb.toString();
    }

    private String buildInsightPrompt(String username, List<HabitListResponse> habits, List<HabitTrackerLog> recentLogs) {
        StringBuilder sb = new StringBuilder();
        sb.append("User: ").append(username).append("\n");
        sb.append("Habits:\n");

        if (habits != null) {
            for (HabitListResponse h : habits) {
                sb.append("- ").append(h.getActivityName())
                        .append(", current streak=").append(h.getCurrentStreak()).append(" days")
                        .append(", longest streak=").append(h.getLongestStreak()).append(" days")
                        .append(", frequency=").append(h.getFrequency()).append("x per week");
                if (h.getLastCompletedDate() != null) {
                    sb.append(", last completed=").append(h.getLastCompletedDate());
                }
                sb.append("\n");
            }
        }

        if (recentLogs != null && !recentLogs.isEmpty()) {
            sb.append("\nRecent activity logs (last 10):\n");
            for (HabitTrackerLog log : recentLogs) {
                sb.append("- Date: ").append(log.getDate_of_activity());
                if (log.getDuration_minutes() > 0) {
                    sb.append(", duration=").append(log.getDuration_minutes()).append(" mins");
                }
                if (log.getDistance_km() > 0) {
                    sb.append(", distance=").append(log.getDistance_km()).append(" km");
                }
                if (log.getCalories_burned() > 0) {
                    sb.append(", calories=").append(log.getCalories_burned());
                }
                if (log.getNotes() != null && !log.getNotes().isBlank()) {
                    sb.append(", notes=").append(log.getNotes());
                }
                sb.append("\n");
            }
        }

        sb.append("\nReturn exactly 2 VIDEO items and exactly 2 BOOK items. ");
        sb.append("Provide personalised insights referencing the user's specific habits, streaks, and recent log details. ");
        sb.append("Tailor all recommendations to the user's habit types. ");
        sb.append("Books must be niche and specific. Avoid all mainstream self-help bestsellers.");

        return sb.toString();
    }
}