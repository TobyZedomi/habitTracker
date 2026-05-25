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
public class ActivityRequest {

    private int habitId;
    private LocalDate dateOfActivity;
    private Integer durationMinutes;
    private Double distanceKm;
    private Integer caloriesBurned;
    private String note;
}
