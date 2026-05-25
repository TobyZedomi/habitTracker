package habitTracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeepSeekRecommendation {
    private String type;
    private String title;
    private String author;
    private String channel;
    private String description;
    private String url;
}