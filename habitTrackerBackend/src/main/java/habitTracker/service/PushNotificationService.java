package habitTracker.service;

import habitTracker.business.DeviceToken;
import habitTracker.persistence.DeviceTokenDao;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    private final DeviceTokenDao deviceTokenDao;
    private final SnsService snsService;

    public void registerToken(String username, String fcmToken, String platform) {
        DeviceToken token = DeviceToken.builder()
                .username(username)
                .fcmToken(fcmToken)
                .platform(platform)
                .build();
        deviceTokenDao.saveToken(token);

        String arn = snsService.registerEndpoint(fcmToken);
        if (arn != null) {
            deviceTokenDao.updateEndpointArn(fcmToken, arn);
            log.info("Token registered and ARN saved for user: {}", username);
        }
    }

    public void sendToUser(String username, String title, String body, String habitId) {
        deviceTokenDao.getTokensByUsername(username).forEach(token -> {
            if (token.getSnsEndpointArn() != null) {
                snsService.sendNotification(token.getSnsEndpointArn(), title, body, habitId);
            } else {
                log.warn("No SNS endpoint ARN for token belonging to: {}", username);
            }
        });
    }
}