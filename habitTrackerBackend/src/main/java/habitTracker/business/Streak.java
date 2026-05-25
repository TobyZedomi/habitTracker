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

public class Streak {

    @EqualsAndHashCode.Include
    private int streak_id;
    private int habit_id;
    private int current_streak;
    private int longest_streak;
    private LocalDate last_completed_date;

}