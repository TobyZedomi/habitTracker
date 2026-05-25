package habitTracker.service;

import habitTracker.business.ActivityType;
import habitTracker.business.Habit;
import habitTracker.business.HabitTrackerLog;
import habitTracker.business.Streak;
import habitTracker.dto.ActivityLogCardResponse;
import habitTracker.dto.UpdateLogRequest;
import habitTracker.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitTrackerLogService {

    private final HabitTrackerLogDao habitTrackerLogDao;
    private final HabitDao habitDao;
    private final StreakDao streakDao;
    private final ActivityTypeDao activityTypeDao;
    private final AiInsightDao aiInsightDao;
    private final AiRecommendationDao aiRecommendationDao;
    private final AiCoachService aiCoachService;

    public int addToHabitTracker(HabitTrackerLog habitTrackerLog, String username) {

        Habit habit = habitDao.getHabitById(habitTrackerLog.getHabit_id());

        if (habit == null) {
            throw new RuntimeException("Habit doesnt exist");
        }

        if (!username.equals(habit.getUsername())) {
            throw new RuntimeException("This user not found");
        }

        int addHabit = habitTrackerLogDao.addHabitToTracker(habitTrackerLog);

        if (addHabit <= 0) {
            throw new RuntimeException("Habit didnt get added");
        }

        updateStreakAfterAddingHabit(habitTrackerLog);

        aiInsightDao.markInsightsAsStale(username);
        aiRecommendationDao.markRecommendationsAsStale(username);

        new Thread(() -> aiCoachService.generateInsightsForUser(username, true)).start();

        return addHabit;
    }

    private void updateStreakAfterAddingHabit(HabitTrackerLog habitTrackerLog) {
        Streak streak = streakDao.findStreakByHabitId(habitTrackerLog.getHabit_id());
        LocalDate completedDate = habitTrackerLog.getDate_of_activity();

        if (streak == null) {
            Streak newStreak = new Streak();
            newStreak.setHabit_id(habitTrackerLog.getHabit_id());
            newStreak.setCurrent_streak(1);
            newStreak.setLongest_streak(1);
            newStreak.setLast_completed_date(completedDate);
            streakDao.addStreak(newStreak);
            return;
        }

        LocalDate lastCompletedDate = streak.getLast_completed_date();

        if (lastCompletedDate == null) {
            streak.setCurrent_streak(1);
        } else if (lastCompletedDate.plusDays(1).equals(completedDate)) {
            streak.setCurrent_streak(streak.getCurrent_streak() + 1);
        } else if (!lastCompletedDate.equals(completedDate)) {
            streak.setCurrent_streak(1);
        }

        if (streak.getCurrent_streak() > streak.getLongest_streak()) {
            streak.setLongest_streak(streak.getCurrent_streak());
        }

        streak.setLast_completed_date(completedDate);
        streakDao.updateCurrentStreak(streak.getCurrent_streak(), habitTrackerLog.getHabit_id());
        streakDao.updateLongestStreak(streak.getLongest_streak(), habitTrackerLog.getHabit_id());
        streakDao.updateLastCompletedDateOfStreak(streak.getLast_completed_date(), habitTrackerLog.getHabit_id());
    }

    public List<HabitTrackerLog> getAllHabitsLoggedByUsername(String username) {

        List<HabitTrackerLog> habitsLogged = habitTrackerLogDao.getAllHabitsByUsername(username);

        if (habitsLogged == null) {
            throw new RuntimeException("Habit doesnt exist for that user");
        }

        return habitsLogged;
    }

    public List<HabitTrackerLog> getAParticularHabitTrackedById(int habitId, String username) {
        Habit habit = habitDao.getHabitById(habitId);

        if (habit == null) {
            throw new RuntimeException("Habit doesnt exist for that user");
        }

        if (!habit.getUsername().equalsIgnoreCase(username)) {
            throw new RuntimeException(username + " doesnt have that Habit");
        }

        return habitTrackerLogDao.getAllHabitsByHabitId(habitId);
    }

    public List<ActivityLogCardResponse> getWeeklyActivityCards(String username) {
        List<Habit> habits = habitDao.getAllHabitsByUsername(username);
        List<ActivityLogCardResponse> cards = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        for (Habit habit : habits) {
            if (!habit.is_active()) {
                continue;
            }

            ActivityType activityType = activityTypeDao.getActivityById(habit.getActivity_type_id());

            String activityName = "Unknown Activity";
            String activityStatusText = "";

            if (activityType != null) {
                activityName = activityType.getName();
                activityStatusText = activityType.getActivity_done();
            }

            int weeklyCount = habitTrackerLogDao.countLogsForHabitInWeek(habit.getHabit_id(), weekStart, weekEnd);
            int frequency = habit.getHabit_frequency();
            boolean completed = weeklyCount >= frequency;
            String progressLabel = weeklyCount + "/" + frequency;

            ActivityLogCardResponse card = ActivityLogCardResponse.builder()
                    .habitId(habit.getHabit_id())
                    .activityName(activityName)
                    .activityStatusText(activityStatusText)
                    .frequency(frequency)
                    .weeklyCompletedCount(weeklyCount)
                    .weeklyGoalCompleted(completed)
                    .progressLabel(progressLabel)
                    .build();

            cards.add(card);
        }

        return cards;
    }

    public List<HabitTrackerLog> getAllRecentHabitsByUsername(String username) {

        List<HabitTrackerLog> recentHabits = habitTrackerLogDao.getAllHabitsByUsernameLimitBy10(username);

        if (recentHabits == null) {
            throw new RuntimeException("Habit doesnt exist for that user");
        }

        return recentHabits;
    }

    public int updateHabitLog(UpdateLogRequest request) {
        return habitTrackerLogDao.updateHabitLog(
                request.getHabitTrackerLogId(),
                request.getDurationMinutes(),
                request.getDistanceKm(),
                request.getCaloriesBurned(),
                request.getNotes()
        );
    }

    public int deleteHabitLog(int habitId) {
        return habitTrackerLogDao.deleteHabitLogged(habitId);
    }
}