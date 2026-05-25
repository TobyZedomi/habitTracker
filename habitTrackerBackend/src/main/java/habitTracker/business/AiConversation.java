package habitTracker.business;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiConversation {

    @EqualsAndHashCode.Include
    private int conversation_id;

    private String username;

    private String conversation_uuid;

    private String s3_key;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;
}
