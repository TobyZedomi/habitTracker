package habitTracker.business;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceToken {

    private int id;
    private String username;
    private String fcmToken;
    private String snsEndpointArn;
    private String platform;
    private LocalDateTime createdAt;
}
