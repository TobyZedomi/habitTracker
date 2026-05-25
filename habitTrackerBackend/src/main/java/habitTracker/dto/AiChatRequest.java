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
public class AiChatRequest {

    private String message;
    private String conversationId;
    private List<String> conversationHistory;


}
