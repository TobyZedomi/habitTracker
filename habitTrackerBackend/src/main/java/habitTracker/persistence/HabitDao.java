package habitTracker.persistence;

import habitTracker.business.Habit;

import java.util.ArrayList;

public interface HabitDao {

    public ArrayList<Habit> getAllHabitsByUsername(String username);

    public Habit getHabitById(int id);

    public int addHabit(Habit habit);

    public int updateHabit(Habit habit);

    public int deleteHabit(String username, int habitId);

    public Habit getHabitByIdAndUsername(String username, int id);

    public Habit getLatestHabitForUser(String username);

    public ArrayList<Habit> getAllActiveHabitsWithReminders();
}
