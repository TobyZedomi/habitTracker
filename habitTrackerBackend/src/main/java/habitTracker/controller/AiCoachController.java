package habitTracker.controller;

import habitTracker.dto.AiChatRequest;
import habitTracker.dto.AiChatResponse;
import habitTracker.dto.AiInsightResponse;
import habitTracker.dto.AiRecommendation;
import habitTracker.service.AiCoachService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiCoachController {

    private final AiCoachService aiCoachService;

    public AiCoachController(AiCoachService aiCoachService) {
        this.aiCoachService = aiCoachService;
    }


    @GetMapping("/getInsights")
    public ResponseEntity<AiInsightResponse> getInsights(Authentication authentication) {

        String username = authentication.getName();

        AiInsightResponse response = aiCoachService.generateInsightsForUser(username, false);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/refreshInsights")
    public ResponseEntity<AiInsightResponse> refreshInsights(Authentication authentication) {
        String username = authentication.getName();
        AiInsightResponse response = aiCoachService.generateInsightsForUser(username, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request, Authentication authentication) {

        String username = authentication.getName();

        AiChatResponse response = aiCoachService.chatWithCoach(username, request);

        return ResponseEntity.ok(response);
    }

}