package habitTracker.persistence;

import habitTracker.business.HabitTrackerLog;

import java.time.LocalDate;
import java.util.ArrayList;

public interface HabitTrackerLogDao {

    public int addHabitToTracker(HabitTrackerLog habitTrackerLog);

    public ArrayList<HabitTrackerLog> getAllHabitsByHabitId(int id);

    public ArrayList<HabitTrackerLog> getAllHabitsByUsername(String username);


    public ArrayList<HabitTrackerLog> getAllHabitsByUsernameLimitBy10(String username);


    public HabitTrackerLog getHabitTrackerLogById(int id);

    public int deleteHabitLogged(int habitId);

    public int countLogsForHabitInWeek(int habitId, LocalDate weekStart, LocalDate weekEnd);

    public ArrayList<HabitTrackerLog> getAllHabitsByUsernameAndDays(String username, int days);

    public HabitTrackerLog getLatestLogByUsername(String username);

    public int updateHabitLog(int habitTrackerLogId, int durationMinutes, double distanceKm, int caloriesBurned, String notes);

}
