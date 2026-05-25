package habitTracker.persistence;

import habitTracker.business.ActivityType;

import java.util.ArrayList;

public interface ActivityTypeDao {

    public ArrayList<ActivityType> getAllActivityTypes();

    public ActivityType getActivityById(int id);

}
