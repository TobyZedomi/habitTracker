package habitTracker.controller;

import habitTracker.business.User;
import habitTracker.dto.AuthResponse;
import habitTracker.dto.LoginRequest;
import habitTracker.dto.RegisterRequest;
import habitTracker.persistence.UserDao;
import habitTracker.service.JwtService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserDao userDao;
    private final JwtService jwtService;

    public AuthController(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return new AuthResponse(false, "Username is required", null);
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return new AuthResponse(false, "Password is required", null);
        }

        if (userDao.usernameExists(request.getUsername())) {
            return new AuthResponse(false, "Username already exists", null);
        }

        if (userDao.emailExists(request.getEmail())) {
            return new AuthResponse(false, "Email already exists", null);
        }

        User user = User.builder()
                .username(request.getUsername())
                .display_name(request.getDisplayName())
                .email(request.getEmail())
                .password(request.getPassword())
                .dateOfBirth(request.getDateOfBirth())
                .isAdmin(false)
                .createdAt(LocalDateTime.now())
                .user_image(request.getUser_image())
                .build();

        int result = userDao.registerUser(user);

        if (result <= 0) {
            return new AuthResponse(false, "Could not register user", null);
        }

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(true, "Registration successful", token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        String storedHash = userDao.getPasswordByUsername(request.getUsername());

        if (storedHash == null) {
            return new AuthResponse(false, "Invalid username or password", null);
        }

        boolean valid = BCrypt.checkpw(request.getPassword(), storedHash);

        if (!valid) {
            return new AuthResponse(false, "Invalid username or password", null);
        }

        String token = jwtService.generateToken(request.getUsername());
        return new AuthResponse(true, "Login successful", token);
    }
}