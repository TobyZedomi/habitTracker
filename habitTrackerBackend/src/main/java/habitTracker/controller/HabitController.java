package habitTracker.controller;

import habitTracker.business.Habit;
import habitTracker.dto.HabitListResponse;
import habitTracker.dto.HabitRequest;
import habitTracker.service.HabitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping("/getAllHabitsForLoggedInUser")
    public List<HabitListResponse> getAllHabitsForLoggedInUser(Authentication authentication) {
        String username = authentication.getName();
        return habitService.getAllHabitsForUser(username);
    }

    @GetMapping("/getOneHabitById/{habitId}")
    public HabitListResponse getOneHabitById(@PathVariable int habitId, Authentication authentication) {
        String username = authentication.getName();
        return habitService.getOneHabitDetail(username, habitId);
    }

    @PostMapping("/createHabit")
    public ResponseEntity<String> createHabit(@RequestBody HabitRequest request, Authentication authentication) {
        String username = authentication.getName();


        if (request.getFrequency() <= 0) {
            return ResponseEntity.badRequest().body("Frequency cannot be zero or negative");
        }
        if (request.getTarget() <= 0) {
            return ResponseEntity.badRequest().body("Target cannot be zero or negative");
        }
        Habit habit = Habit.builder()
                .username(username)
                .activity_type_id(request.getActivityTypeId())
                .description(request.getDescription())
                .habit_reminder(request.getReminder())
                .habit_target(request.getTarget())
                .habit_frequency(request.getFrequency())
                .is_active(true)
                .build();

        int result = habitService.createHabit(habit);

        if (result > 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Habit Created");
        }

        return ResponseEntity.badRequest().body("Failed to create habit");
    }

    @PutMapping("/updateHabit")
    public ResponseEntity<String> updateHabit(@RequestBody HabitRequest request, Authentication authentication) {
        String username = authentication.getName();

        Habit existingHabit = habitService.getHabitByIdAndUsername(username, request.getHabitId());

        if (existingHabit == null) {
            return ResponseEntity.badRequest().body("Habit not found");
        }

        if (request.getFrequency() <= 0) {
            return ResponseEntity.badRequest().body("Frequency cannot be zero or negative");
        }
        if (request.getTarget() <= 0) {
            return ResponseEntity.badRequest().body("Target cannot be zero or negative");
        }

        Habit habit = Habit.builder()
                .habit_id(request.getHabitId())
                .username(username)
                .activity_type_id(request.getActivityTypeId())
                .description(request.getDescription())
                .habit_reminder(request.getReminder())
                .habit_target(request.getTarget())
                .habit_frequency(request.getFrequency())
                .is_active(existingHabit.is_active())
                .build();

        int result = habitService.updateHabit(habit);

        if (result > 0) {
            return ResponseEntity.ok("Habit updated");
        }

        return ResponseEntity.badRequest().body("Failed to update the habit");
    }

    @DeleteMapping("/deleteHabit")
    public ResponseEntity<String> deleteHabit(@RequestBody HabitRequest request, Authentication authentication) {
        String username = authentication.getName();

        int result = habitService.deleteHabit(username, request.getHabitId());

        if (result > 0) {
            return ResponseEntity.ok("Habit deleted");
        }

        return ResponseEntity.badRequest().body("Failed to delete habit");
    }

    @GetMapping("/latest")
    public Habit getLatestHabit(Authentication authentication) {
        String username = authentication.getName();
        return habitService.getLatestHabitForUser(username);
    }
}
