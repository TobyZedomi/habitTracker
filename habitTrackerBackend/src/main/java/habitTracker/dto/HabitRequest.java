package habitTracker.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HabitRequest {

    private Integer habitId;
    private Integer activityTypeId;

    private String description;

    private String reminder;

    private Integer target;

    private Integer frequency;

}