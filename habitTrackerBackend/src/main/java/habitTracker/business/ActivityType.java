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

public class ActivityType {

    @EqualsAndHashCode.Include
    private int activity_type_id;
    private String name;
    private String activity_done;



}