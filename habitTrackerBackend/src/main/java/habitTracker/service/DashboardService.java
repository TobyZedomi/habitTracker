package habitTracker.service;
import habitTracker.business.ActivityType;
import habitTracker.business.Habit;
import habitTracker.business.HabitTrackerLog;
import habitTracker.business.Streak;
import habitTracker.dto.ActivityResponse;
import habitTracker.dto.DashboardResponse;
import habitTracker.dto.HabitListResponse;
import habitTracker.persistence.ActivityTypeDao;
import habitTracker.persistence.HabitDao;
import habitTracker.persistence.HabitTrackerLogDao;
import habitTracker.persistence.StreakDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final HabitTrackerLogDao habitTrackerLogDao;

    private final HabitDao habitDao;

    private final StreakDao streakDao;

    private final ActivityTypeDao activityTypeDao;


    public DashboardResponse getDashboardForUser(String username, int days){

        List<Habit> habitsForUser = habitDao.getAllHabitsByUsername(username);

        List<HabitTrackerLog> allHabitsLoggedByUser = days > 0 ? habitTrackerLogDao.getAllHabitsByUsernameAndDays(username, days) : habitTrackerLogDao.getAllHabitsByUsername(username);
        List<HabitTrackerLog> recentHabitLogsForUser = habitTrackerLogDao.getAllHabitsByUsernameLimitBy10(username);

        List<HabitListResponse> habitResponses = new ArrayList<>();
        List<ActivityResponse> recentActivityResponses = new ArrayList<>();

        int totalHabits = habitsForUser.size();
        int activeHabits = 0;
        int totalCompletedSessions = 0;
        int totalDurationMinutes = 0;
        double totalDistanceKm = 0;
        int totalCaloriesBurned = 0;

        for (int i = 0; i < habitsForUser.size();i++) {


            Habit habit = habitsForUser.get(i);

            if (habit.is_active()) {
                activeHabits++;
            }

            Streak streakForHabit = streakDao.findStreakByHabitId(habit.getHabit_id());
            ActivityType activityType = activityTypeDao.getActivityById(habit.getActivity_type_id());

            HabitListResponse habitResponse = new HabitListResponse();
            habitResponse.setHabitId(habit.getHabit_id());
            habitResponse.setActivityName(activityType.getName());
            habitResponse.setActivityStatusText(activityType.getActivity_done());
            habitResponse.setDescription(habit.getDescription());
            habitResponse.setReminder(habit.getHabit_reminder());
            habitResponse.setTarget(habit.getHabit_target());
            habitResponse.setFrequency(habit.getHabit_frequency());
            habitResponse.setActive(habit.is_active());

            if (streakForHabit != null) {
                habitResponse.setCurrentStreak(streakForHabit.getCurrent_streak());
                habitResponse.setLongestStreak(streakForHabit.getLongest_streak());
                habitResponse.setLastCompletedDate(streakForHabit.getLast_completed_date());
            } else {
                habitResponse.setCurrentStreak(0);
                habitResponse.setLongestStreak(0);
                habitResponse.setLastCompletedDate(null);
            }

            habitResponses.add(habitResponse);
        }

        for (int i = 0; i < allHabitsLoggedByUser.size();i++) {

            HabitTrackerLog habitTrackerLog = allHabitsLoggedByUser.get(i);
            totalCompletedSessions++;
            totalDurationMinutes += habitTrackerLog.getDuration_minutes();
            totalDistanceKm += habitTrackerLog.getDistance_km();
            totalCaloriesBurned += habitTrackerLog.getCalories_burned();
        }

        for (int i = 0; i < recentHabitLogsForUser.size();i++) {

            HabitTrackerLog habitTrackerLog = recentHabitLogsForUser.get(i);

            Habit habit = habitDao.getHabitById(habitTrackerLog.getHabit_id());
            ActivityType activityType = activityTypeDao.getActivityById(habit.getActivity_type_id());

            ActivityResponse activityResponse = new ActivityResponse();
            activityResponse.setHabitTrackerLogId(habitTrackerLog.getHabit_tracker_log_id());
            activityResponse.setHabitId(habitTrackerLog.getHabit_id());
            activityResponse.setActivityName(activityType.getName());
            activityResponse.setDate(habitTrackerLog.getDate_of_activity());
            activityResponse.setDuration(habitTrackerLog.getDuration_minutes());
            activityResponse.setDistanceKm(habitTrackerLog.getDistance_km());
            activityResponse.setCaloriesBurned(habitTrackerLog.getCalories_burned());
            activityResponse.setNotes(habitTrackerLog.getNotes());
            activityResponse.setCreatedAt(habitTrackerLog.getCreated_at());

            recentActivityResponses.add(activityResponse);
        }

        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setTotalHabits(totalHabits);
        dashboardResponse.setActiveHabits(activeHabits);
        dashboardResponse.setTotalCompletedSessions(totalCompletedSessions);
        dashboardResponse.setTotalDurationMinutes(totalDurationMinutes);
        dashboardResponse.setTotalDistanceKm(totalDistanceKm);
        dashboardResponse.setTotalCaloriesBurned(totalCaloriesBurned);
        dashboardResponse.setHabits(habitResponses);
        dashboardResponse.setRecentActivities(recentActivityResponses);

        return dashboardResponse;


    }


}
