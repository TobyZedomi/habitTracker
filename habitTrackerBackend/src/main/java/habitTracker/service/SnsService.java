package habitTracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointRequest;
import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
public class SnsService {

    private static final Logger log = LoggerFactory.getLogger(SnsService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SnsClient snsClient;

    @Value("${aws.sns.platform.application.arn}")
    private String platformApplicationArn;

    public SnsService(@Value("${aws.region}") String region) {
        this.snsClient = SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public String registerEndpoint(String fcmToken) {
        try {
            CreatePlatformEndpointRequest request = CreatePlatformEndpointRequest.builder()
                    .platformApplicationArn(platformApplicationArn)
                    .token(fcmToken)
                    .build();
            CreatePlatformEndpointResponse response = snsClient.createPlatformEndpoint(request);
            log.info("SNS endpoint created: {}", response.endpointArn());
            return response.endpointArn();
        } catch (Exception e) {
            log.error("Failed to create SNS endpoint: {}", e.getMessage());
            return null;
        }
    }

    public void sendNotification(String endpointArn, String title, String body, String habitId) {
        try {
            ObjectNode notification = objectMapper.createObjectNode();
            notification.put("title", title);
            notification.put("body", body);

            ObjectNode data = objectMapper.createObjectNode();
            data.put("habitId", habitId);

            ObjectNode gcmPayload = objectMapper.createObjectNode();
            gcmPayload.set("notification", notification);
            gcmPayload.set("data", data);

            ObjectNode snsMessage = objectMapper.createObjectNode();
            snsMessage.put("GCM", objectMapper.writeValueAsString(gcmPayload));

            String messageJson = objectMapper.writeValueAsString(snsMessage);
            log.info("Sending SNS message: {}", messageJson);

            PublishRequest request = PublishRequest.builder()
                    .targetArn(endpointArn)
                    .message(messageJson)
                    .messageStructure("json")
                    .build();

            PublishResponse response = snsClient.publish(request);
            log.info("Notification sent via SNS, messageId: {}", response.messageId());
        } catch (Exception e) {
            log.error("Failed to send SNS notification: {}", e.getMessage());
        }
    }
}