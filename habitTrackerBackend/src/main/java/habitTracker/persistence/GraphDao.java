package habitTracker.persistence;

import habitTracker.dto.GraphDataPoint;

import java.util.List;

public interface GraphDao {
    List<GraphDataPoint> getDailyCompletionsLast30Days(String username);
    List<GraphDataPoint> getWeeklyCompletionsLast12Weeks(String username);
}