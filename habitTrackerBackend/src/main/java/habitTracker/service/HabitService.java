package habitTracker.service;

import habitTracker.business.ActivityType;
import habitTracker.business.Habit;
import habitTracker.business.Streak;
import habitTracker.dto.HabitListResponse;
import habitTracker.persistence.ActivityTypeDao;
import habitTracker.persistence.HabitDao;
import habitTracker.persistence.StreakDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import habitTracker.persistence.AiInsightDao;
import habitTracker.persistence.AiRecommendationDao;

@Service
@RequiredArgsConstructor
public class HabitService {


    private final HabitDao habitDao;

    private final StreakDao streakDao;

    private final ActivityTypeDao activityTypeDao;

    private final AiInsightDao aiInsightDao;
    private final AiRecommendationDao aiRecommendationDao;


    public List<HabitListResponse> getAllHabitsForUser(String username){

        List<Habit> habitsForUser = habitDao.getAllHabitsByUsername(username);

        List<HabitListResponse> habitListResponseList = new ArrayList<>();

        for (int i = 0; i < habitsForUser.size();i++) {

            Habit habit = habitsForUser.get(i);
            Streak streakForHabit = streakDao.findStreakByHabitId(habit.getHabit_id());

            ActivityType activityType = activityTypeDao.getActivityById(habit.getActivity_type_id());

            HabitListResponse habitListResponse2 = new HabitListResponse();
            habitListResponse2.setHabitId(habit.getHabit_id());
            habitListResponse2.setActivityTypeId(habit.getActivity_type_id());
            habitListResponse2.setActivityName(activityType.getName());
            habitListResponse2.setActivityStatusText(activityType.getActivity_done());
            habitListResponse2.setDescription(habit.getDescription());
            habitListResponse2.setReminder(habit.getHabit_reminder());
            habitListResponse2.setTarget(habit.getHabit_target());
            habitListResponse2.setFrequency(habit.getHabit_frequency());
            habitListResponse2.setActive(habit.is_active());

            if (streakForHabit != null) {
                habitListResponse2.setCurrentStreak(streakForHabit.getCurrent_streak());
                habitListResponse2.setLongestStreak(streakForHabit.getLongest_streak());
                habitListResponse2.setLastCompletedDate(streakForHabit.getLast_completed_date());
            } else {
                habitListResponse2.setCurrentStreak(0);
                habitListResponse2.setLongestStreak(0);
                habitListResponse2.setLastCompletedDate(null);
            }
            habitListResponseList.add(habitListResponse2);
        }

       return habitListResponseList;
    }


    // get one habit detail


    public HabitListResponse getOneHabitDetail (String username, int habitId){


        Habit habit = habitDao.getHabitByIdAndUsername(username, habitId);

        if (habit == null){

            return null;
        }

        Streak streakForHabit = streakDao.findStreakByHabitId(habit.getHabit_id());


        ActivityType activityType = activityTypeDao.getActivityById(habit.getActivity_type_id());

        HabitListResponse habitListResponse = new HabitListResponse();
        habitListResponse.setHabitId(habit.getHabit_id());
        habitListResponse.setActivityTypeId(habit.getActivity_type_id());
        habitListResponse.setActivityName(activityType.getName());
        habitListResponse.setActivityStatusText(activityType.getActivity_done());
        habitListResponse.setDescription(habit.getDescription());
        habitListResponse.setReminder(habit.getHabit_reminder());
        habitListResponse.setTarget(habit.getHabit_target());
        habitListResponse.setFrequency(habit.getHabit_frequency());
        habitListResponse.setActive(habit.is_active());

        if (streakForHabit != null) {
            habitListResponse.setCurrentStreak(streakForHabit.getCurrent_streak());
            habitListResponse.setLongestStreak(streakForHabit.getLongest_streak());
            habitListResponse.setLastCompletedDate(streakForHabit.getLast_completed_date());
        } else {
            habitListResponse.setCurrentStreak(0);
            habitListResponse.setLongestStreak(0);
            habitListResponse.setLastCompletedDate(null);
        }

        return habitListResponse;
    }

    // create habit

    public int createHabit(Habit habit){


      int createHabit = habitDao.addHabit(habit);

        return createHabit;
    }

    // update habit

    public int updateHabit(Habit habit) {

        int updateHabit = habitDao.updateHabit(habit);

        return updateHabit;

    }

    // delete habit

    public int deleteHabit(String username, int habitId) {
        int deleteHabit = habitDao.deleteHabit(username, habitId);
        aiInsightDao.markInsightsAsStale(username);
        aiRecommendationDao.markRecommendationsAsStale(username);
        return deleteHabit;
    }


    // habit by id and username

    public Habit getHabitByIdAndUsername(String username, int habitId){
        return habitDao.getHabitByIdAndUsername(username, habitId);
    }



    // get latest habit for user

    public Habit getLatestHabitForUser(String username){

        return habitDao.getLatestHabitForUser(username);
    }


}
