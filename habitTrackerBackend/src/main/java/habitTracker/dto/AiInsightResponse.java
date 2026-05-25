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
public class AiInsightResponse {

    private String insightText;
    private List<AiRecommendation> recommendations;
    private long generatedAt = System.currentTimeMillis();
}
