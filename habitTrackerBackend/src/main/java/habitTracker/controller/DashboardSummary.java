package habitTracker.controller;

import habitTracker.business.HabitTrackerLog;
import habitTracker.dto.ActivityResponse;
import habitTracker.dto.DashboardResponse;
import habitTracker.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardSummary {



    private final DashboardService dashboardService;

    public DashboardSummary(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }



    // get dashboard summary

    @GetMapping("/getDashboardSummary")
    public DashboardResponse getDashboardSummary(Authentication authentication, @RequestParam(value = "days", defaultValue = "0") int days) {
        String username = authentication.getName();
        return dashboardService.getDashboardForUser(username, days);
    }

    // get dashboard fo recent activities

    @GetMapping("/getDashboardRecentActivities")
    public List<ActivityResponse> getDashboardRecentActivities(Authentication authentication) {
        String username = authentication.getName();
        DashboardResponse dashboardResponse =  dashboardService.getDashboardForUser(username, 0);

        return dashboardResponse.getRecentActivities();
    }

}
