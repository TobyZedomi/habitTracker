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

public class Habit {

    @EqualsAndHashCode.Include
    private int habit_id;
    private String username;
    private int activity_type_id;
    private String description;

    private String habit_reminder;

    private int habit_target;

    private int habit_frequency;

    private boolean  is_active;

}