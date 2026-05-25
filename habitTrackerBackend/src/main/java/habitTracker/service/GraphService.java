package habitTracker.service;

import habitTracker.dto.GraphDataPoint;
import habitTracker.persistence.GraphDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphService {

    private final GraphDao graphDao;

    public List<GraphDataPoint> getDailyCompletions(String username) {
        return graphDao.getDailyCompletionsLast30Days(username);
    }

    public List<GraphDataPoint> getWeeklyCompletions(String username) {
        return graphDao.getWeeklyCompletionsLast12Weeks(username);
    }
}
