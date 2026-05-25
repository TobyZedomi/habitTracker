package habitTracker.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {

    private int totalHabits;

    private int activeHabits;

    private int totalCompletedSessions;

    private int totalDurationMinutes;

    private double totalDistanceKm;

    private int totalCaloriesBurned;

    private List<HabitListResponse> habits;

    private List<ActivityResponse> recentActivities;
}
