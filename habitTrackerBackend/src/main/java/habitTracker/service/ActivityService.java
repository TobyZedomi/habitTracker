package habitTracker.service;

import habitTracker.business.ActivityType;
import habitTracker.business.Habit;
import habitTracker.business.Streak;
import habitTracker.dto.ActivityTypeResponse;
import habitTracker.dto.HabitListResponse;
import habitTracker.persistence.ActivityTypeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {


    private final ActivityTypeDao activityTypeDao;

    public List<ActivityTypeResponse> getAllActivityTypes() {

        List<ActivityType> allActivityTypes = activityTypeDao.getAllActivityTypes();
        List<ActivityTypeResponse> activityTypeResponses = new ArrayList<>();

        for (int i = 0; i < allActivityTypes.size();i++) {
            ActivityTypeResponse response = new ActivityTypeResponse();
            response.setActivityTypeId(allActivityTypes.get(i).getActivity_type_id());
            response.setName(allActivityTypes.get(i).getName());
            response.setActivityDone(allActivityTypes.get(i).getActivity_done());

            activityTypeResponses.add(response);
        }

        return activityTypeResponses;
    }
}
