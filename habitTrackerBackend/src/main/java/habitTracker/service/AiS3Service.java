package habitTracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import habitTracker.dto.AiChatResponse;
import habitTracker.dto.AiInsightResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AiS3Service {

    private static final Logger log = LoggerFactory.getLogger(AiS3Service.class);

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region:eu-west-1}")
    private String region;

    @Value("${aws.access.key}")
    private String accessKey;

    @Value("${aws.secret.key}")
    private String secretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    public String saveConversation(String username, String conversationId, AiChatResponse response, String userMessage) {
        String key = "conversations/" + username + "/" + conversationId + ".json";
        try {
            String existing = getFromS3(key);
            List<Map<String, String>> turns = new ArrayList<>();
            if (existing != null) {
                turns = objectMapper.readValue(existing, List.class);
            }

            Map<String, String> userTurn = new HashMap<>();
            userTurn.put("role", "user");
            userTurn.put("content", userMessage);
            turns.add(userTurn);

            Map<String, String> assistantTurn = new HashMap<>();
            assistantTurn.put("role", "assistant");
            assistantTurn.put("content", response.getReply());
            turns.add(assistantTurn);

            putToS3(key, objectMapper.writeValueAsString(turns));
            log.info("Saved conversation to S3: {}", key);
        } catch (Exception e) {
            log.error("Failed to save conversation to S3 for user {}: {}", username, e.getMessage(), e);
        }
        return key;
    }

    public List<Object> loadConversationHistory(String username, String conversationId) {
        String key = "conversations/" + username + "/" + conversationId + ".json";
        try {
            String content = getFromS3(key);
            if (content != null) {
                return objectMapper.readValue(content, List.class);
            }
        } catch (Exception e) {
            log.error("Failed to load conversation from S3 for user {}: {}", username, e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public String generateConversationId() {
        return UUID.randomUUID().toString();
    }

    private void putToS3(String key, String content) {
        S3Client s3 = s3Client();
        try {
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType("application/json")
                            .build(),
                    RequestBody.fromString(content, StandardCharsets.UTF_8)
            );
        } finally {
            s3.close();
        }
    }

    private String getFromS3(String key) {
        S3Client s3 = s3Client();
        try {
            byte[] bytes = s3.getObjectAsBytes(
                    GetObjectRequest.builder().bucket(bucket).key(key).build()
            ).asByteArray();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (NoSuchKeyException e) {
            return null;
        } finally {
            s3.close();
        }
    }
}