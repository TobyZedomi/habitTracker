package habitTracker.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityLogCardResponse {

    @EqualsAndHashCode.Include
    private int habitId;

    private String activityName;

    private String activityStatusText;

    private int frequency;

    private int weeklyCompletedCount;

    private boolean weeklyGoalCompleted;

    private String progressLabel;
}