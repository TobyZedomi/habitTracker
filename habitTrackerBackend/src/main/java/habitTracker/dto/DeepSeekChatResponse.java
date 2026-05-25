package habitTracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeepSeekChatResponse {
    private String reply;
    private List<DeepSeekRecommendation> recommendations;
}
