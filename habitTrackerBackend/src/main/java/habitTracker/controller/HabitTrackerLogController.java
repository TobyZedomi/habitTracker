package habitTracker.controller;

import habitTracker.business.HabitTrackerLog;
import habitTracker.dto.*;
import habitTracker.service.HabitTrackerLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/habitTrackerLog")
public class HabitTrackerLogController {

    private final HabitTrackerLogService habitTrackerLogService;

    public HabitTrackerLogController(HabitTrackerLogService habitTrackerLogService) {
        this.habitTrackerLogService = habitTrackerLogService;
    }


    // log a completed activity


    @PostMapping("/logHabit")
    public ResponseEntity<String> logHabit(@RequestBody ActivityRequest request, Authentication authentication){


        String username = authentication.getName();

        if (request.getDurationMinutes() != null && request.getDurationMinutes() < 0) {
            return ResponseEntity.badRequest().body("Duration cannot be negative");
        }
        if (request.getDistanceKm() != null && request.getDistanceKm() < 0) {
            return ResponseEntity.badRequest().body("Distance cannot be negative");
        }
        if (request.getCaloriesBurned() != null && request.getCaloriesBurned() < 0) {
            return ResponseEntity.badRequest().body("Calories cannot be negative");
        }

        HabitTrackerLog habitTrackerLog = HabitTrackerLog.builder()
                .habit_id(request.getHabitId())
                .date_of_activity(request.getDateOfActivity())
                .duration_minutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 0)
                .distance_km(request.getDistanceKm() != null ? request.getDistanceKm() : 0.0)
                .calories_burned(request.getCaloriesBurned() != null ? request.getCaloriesBurned() : 0)
                .notes(request.getNote())
                .created_at(java.time.LocalDateTime.now())
                .build();


        int result = habitTrackerLogService.addToHabitTracker(habitTrackerLog, username);

        if (result > 0){
            return ResponseEntity.status(HttpStatus.CREATED).body("Activity Logged");
        }

        return ResponseEntity.badRequest().body("Failed to log activity");
    }


    // get logs for a habit

    @GetMapping("/getAllLogsForAHabit/{habitId}")
    public List<HabitTrackerLog> getAllLogsForAHabit(@PathVariable int habitId, Authentication authentication) {
        String username = authentication.getName();
        return habitTrackerLogService.getAParticularHabitTrackedById(habitId, username);
    }


    @GetMapping("/weekly-cards")
    public ResponseEntity<List<ActivityLogCardResponse>> getWeeklyActivityCards(Authentication authentication) {
        String username = authentication.getName();
        List<ActivityLogCardResponse> cards = habitTrackerLogService.getWeeklyActivityCards(username);
        return ResponseEntity.ok(cards);
    }


    // get recent habits logged by user

    @GetMapping("/getAllRecentLogsForAHabit")
    public List<HabitTrackerLog> getAllRecentLogsForAHabit(Authentication authentication) {
        String username = authentication.getName();
        return habitTrackerLogService.getAllRecentHabitsByUsername(username);
    }


    @GetMapping("/getAllLoggedHabitsForCurrentUser")
    public List<HabitTrackerLog> getAllLoggedHabitsForCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return habitTrackerLogService.getAllHabitsLoggedByUsername(username);
    }


    @PutMapping("/updateHabitLog")
    public ResponseEntity<String> updateHabitLog(@RequestBody UpdateLogRequest updateLogRequest, Authentication authentication) {

        if (updateLogRequest.getDurationMinutes() != null && updateLogRequest.getDurationMinutes() < 0) {
            return ResponseEntity.badRequest().body("Duration cannot be negative");
        }
        if (updateLogRequest.getDistanceKm() != null && updateLogRequest.getDistanceKm() < 0) {
            return ResponseEntity.badRequest().body("Distance cannot be negative");
        }
        if (updateLogRequest.getCaloriesBurned() != null && updateLogRequest.getCaloriesBurned() < 0) {
            return ResponseEntity.badRequest().body("Calories cannot be negative");
        }

        int result = habitTrackerLogService.updateHabitLog(updateLogRequest);
        if (result > 0) {
            return ResponseEntity.ok("Activity log updated");
        }
        return ResponseEntity.badRequest().body("Failed to update activity log");
    }

    @DeleteMapping("/deleteHabitLog")
    public ResponseEntity<String> deleteHabit(@RequestBody DeleteLogRequest request, Authentication authentication) {

        String username = authentication.getName();

        int result = habitTrackerLogService.deleteHabitLog(request.getHabitTrackerLogId());

        if (result > 0) {
            return ResponseEntity.ok("Habit Log deleted");
        }

        return ResponseEntity.badRequest().body("Failed to delete habit log");
    }

}
