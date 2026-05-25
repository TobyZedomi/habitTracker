package habitTracker.dto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiChatResponse {

    private String reply;

    private List<AiRecommendation> recommendations;

    private String conversationId;
    private long timestamp = System.currentTimeMillis();
}
