package habitTracker.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateLogRequest {
    private int habitTrackerLogId;
    private Integer durationMinutes;
    private Double distanceKm;
    private Integer caloriesBurned;
    private String notes;
}
