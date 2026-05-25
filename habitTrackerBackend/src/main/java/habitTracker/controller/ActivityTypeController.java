package habitTracker.controller;

import habitTracker.dto.ActivityTypeResponse;
import habitTracker.service.ActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityTypeController {

    private final ActivityService activityService;

    public ActivityTypeController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/getAllActivityTypes")
    public List<ActivityTypeResponse> getAllActivityTypes() {
        return activityService.getAllActivityTypes();
    }




}
