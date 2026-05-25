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
public class AiInsightRecord {

    @EqualsAndHashCode.Include
    private int insight_id;

    private String username;

    private String insight_text;

    private LocalDateTime created_at;

    private boolean is_current;
}
