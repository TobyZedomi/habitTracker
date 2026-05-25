package habitTracker.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HabitListResponse {

    private int habitId;

    private String activityName;

    private int activityTypeId;

    private String activityStatusText;

    private String description;

    private String reminder;

    private int target;

    private int frequency;

    private boolean isActive;

    private int currentStreak;

    private int longestStreak;

    private LocalDate lastCompletedDate;


}
