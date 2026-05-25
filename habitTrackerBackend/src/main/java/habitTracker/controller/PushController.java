package habitTracker.controller;

import habitTracker.dto.PushTokenRequest;
import habitTracker.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push")
public class PushController {

    private final PushNotificationService pushNotificationService;

    public PushController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody PushTokenRequest request,
                                           Authentication authentication) {
        String username = authentication.getName();
        pushNotificationService.registerToken(username, request.getToken(), request.getPlatform());
        return ResponseEntity.ok("Token registered");
    }
}