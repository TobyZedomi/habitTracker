package habitTracker.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityTypeResponse {

    private int activityTypeId;
    private String name;
    private String activityDone;
}
