package habitTracker.service;

import habitTracker.business.AiConversation;
import habitTracker.business.AiInsightRecord;
import habitTracker.business.AiRecommendationRecord;
import habitTracker.business.HabitTrackerLog;
import habitTracker.dto.AiChatRequest;
import habitTracker.dto.AiChatResponse;
import habitTracker.dto.AiInsightResponse;
import habitTracker.dto.AiRecommendation;
import habitTracker.dto.HabitListResponse;
import habitTracker.persistence.AiConversationDao;
import habitTracker.persistence.AiInsightDao;
import habitTracker.persistence.AiRecommendationDao;
import habitTracker.persistence.HabitTrackerLogDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiCoachService {

    private final AiConversationDao aiConversationDao;
    private final AiRecommendationDao aiRecommendationDao;
    private final AiInsightDao aiInsightDao;
    private final HabitTrackerLogDao habitTrackerLogDao;
    private final DeepSeekService deepSeekService;
    private final AiS3Service aiS3Service;
    private final HabitService habitService;

    public AiInsightResponse generateInsightsForUser(String username) {
        return generateInsightsForUser(username, false);
    }

    public AiInsightResponse generateInsightsForUser(String username, boolean forceRefresh) {

        List<HabitListResponse> habits = habitService.getAllHabitsForUser(username);

        if (habits == null || habits.isEmpty()) {
            AiInsightResponse empty = new AiInsightResponse();
            empty.setInsightText("You have no habits yet. Add your first habit to get personalised insights and recommendations.");
            empty.setRecommendations(new ArrayList<>());
            empty.setGeneratedAt(System.currentTimeMillis());
            return empty;
        }

        if (!forceRefresh) {
            AiInsightRecord existingInsight = aiInsightDao.getLatestInsightByUsername(username);

            if (existingInsight != null) {
                AiInsightResponse cached = new AiInsightResponse();
                cached.setInsightText(existingInsight.getInsight_text());
                cached.setGeneratedAt(existingInsight.getCreated_at().toEpochSecond(java.time.ZoneOffset.UTC) * 1000);
                cached.setRecommendations(getLatestRecommendationsForUser(username));
                return cached;
            }
        }

        List<HabitTrackerLog> recentLogs = habitTrackerLogDao.getAllHabitsByUsernameLimitBy10(username);
        AiInsightResponse response = deepSeekService.generateInsights(username, habits, recentLogs);

        AiInsightRecord insightRecord = new AiInsightRecord();
        insightRecord.setUsername(username);
        insightRecord.setInsight_text(response.getInsightText());
        insightRecord.setCreated_at(LocalDateTime.now());
        insightRecord.set_current(true);
        aiInsightDao.saveInsight(insightRecord);

        if (response.getRecommendations() != null) {
            saveRecommendationsToDb(username, response.getRecommendations());
        }

        return response;
    }

    public AiChatResponse chatWithCoach(String username, AiChatRequest request) {

        List<HabitListResponse> habits = habitService.getAllHabitsForUser(username);
        List<HabitTrackerLog> recentLogs = habitTrackerLogDao.getAllHabitsByUsernameLimitBy10(username);

        String conversationUuid = request.getConversationId();
        if (conversationUuid == null || conversationUuid.isBlank()) {
            conversationUuid = UUID.randomUUID().toString();
        }

        List<String> history = request.getConversationHistory();
        if (history != null && history.size() > 8) {
            List<String> historyShorter = new ArrayList<>();
            for (int i = history.size() - 8; i < history.size(); i++) {
                historyShorter.add(history.get(i));
            }
            history = historyShorter;
        }

        if (habits == null || habits.isEmpty()) {
            AiChatResponse empty = new AiChatResponse();
            empty.setReply("You have no habits yet. Add your first habit and I can start coaching you with personalised advice.");
            empty.setRecommendations(new ArrayList<>());
            return empty;
        }

        AiChatResponse response = deepSeekService.chat(username, request.getMessage(), habits, recentLogs, history);
        response.setConversationId(conversationUuid);

        String s3Key = aiS3Service.saveConversation(username, conversationUuid, response, request.getMessage());

        AiConversation existing = aiConversationDao.getConversationByUuid(conversationUuid);

        if (existing == null) {
            AiConversation conversation = new AiConversation();
            conversation.setUsername(username);
            conversation.setConversation_uuid(conversationUuid);
            conversation.setS3_key(s3Key);
            conversation.setCreated_at(LocalDateTime.now());
            conversation.setUpdated_at(LocalDateTime.now());
            aiConversationDao.saveConversation(conversation);
        }

        return response;
    }

    public List<AiRecommendation> getLatestRecommendationsForUser(String username) {
        List<AiRecommendationRecord> records = aiRecommendationDao.getLatestRecommendationsByUsername(username);
        List<AiRecommendation> recommendations = new ArrayList<>();

        for (AiRecommendationRecord record : records) {
            AiRecommendation rec = new AiRecommendation();
            rec.setType(record.getType());
            rec.setTitle(record.getTitle());
            rec.setAuthor(record.getAuthor());
            rec.setChannel(record.getChannel());
            rec.setDescription(record.getDescription());
            rec.setUrl(record.getUrl());
            recommendations.add(rec);
        }

        return recommendations;
    }

    private void saveRecommendationsToDb(String username, List<AiRecommendation> recommendations) {
        for (AiRecommendation rec : recommendations) {
            AiRecommendationRecord record = new AiRecommendationRecord();
            record.setUsername(username);
            record.setType(rec.getType());
            record.setTitle(rec.getTitle());
            record.setAuthor(rec.getAuthor());
            record.setChannel(rec.getChannel());
            record.setDescription(rec.getDescription());
            record.setUrl(rec.getUrl());
            record.setCreated_at(LocalDateTime.now());
            record.set_current(true);
            aiRecommendationDao.saveRecommendation(record);
        }
    }
}