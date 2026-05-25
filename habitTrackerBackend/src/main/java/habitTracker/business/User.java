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

public class User {

    @EqualsAndHashCode.Include
    private String username;
    private String display_name;
    @ToString.Exclude
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private boolean isAdmin;
    private LocalDateTime createdAt;
    private String user_image;


}