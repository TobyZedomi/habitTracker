package habitTracker.persistence;

import habitTracker.business.Streak;

import java.time.LocalDate;

public interface StreakDao {

    public Streak findStreakByHabitId(int id);

    public int addStreak(Streak streak);

    public int updateCurrentStreak(int currentStreak, int habitId);

    public int updateLongestStreak(int longestStreak, int habitId);


    public int updateLastCompletedDateOfStreak(LocalDate lastDate, int habitId);
}
