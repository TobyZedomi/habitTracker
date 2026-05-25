package habitTracker.controller;

import habitTracker.dto.GraphDataPoint;
import habitTracker.service.GraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/graphs")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/daily")
    public ResponseEntity<List<GraphDataPoint>> getDailyCompletions(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(graphService.getDailyCompletions(username));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<GraphDataPoint>> getWeeklyCompletions(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(graphService.getWeeklyCompletions(username));
    }
}