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
public class AiRecommendation {

    private String type;
    private String title;
    private String author;
    private String channel;
    private String description;
    private String url;
    private String s3Key;
}
