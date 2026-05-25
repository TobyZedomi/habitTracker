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

public class HabitTrackerLog {

    @EqualsAndHashCode.Include
    private int habit_tracker_log_id;
    private int habit_id;
    private LocalDate date_of_activity;
    private int duration_minutes;
    private double distance_km;
    private int calories_burned;
    private String notes;
    private LocalDateTime created_at;

}