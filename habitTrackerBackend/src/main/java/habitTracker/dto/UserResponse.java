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
public class UserResponse {
    private String username;
    private String displayName;
    private String email;
    private LocalDate dateOfBirth;
    private boolean isAdmin;
    private LocalDateTime createdAt;
    private String user_image;
}