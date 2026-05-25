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
public class AiRecommendationRecord {
    @EqualsAndHashCode.Include
    private int recommendation_id;

    private String username;

    private String type;

    private String title;

    private String author;

    private String channel;

    private String description;

    private String url;

    private String content_hash;

    private LocalDateTime created_at;

    private boolean is_current;
}
