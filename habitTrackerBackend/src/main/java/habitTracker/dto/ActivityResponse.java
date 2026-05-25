package habitTracker.dto;
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
public class ActivityResponse {

    private int habitTrackerLogId;

    private int habitId;

    private String activityName;

    private LocalDate date;

    private int duration;

    private double distanceKm;

    private int caloriesBurned;

    private String notes;

    private LocalDateTime createdAt;

}
