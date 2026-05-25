package habitTracker.controller;

import habitTracker.business.User;
import habitTracker.dto.UserResponse;
import habitTracker.persistence.UserDao;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {

        String username = authentication.getName();

        User user = userDao.findUserByUsername(username);

        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getUsername(),
                user.getDisplay_name(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.isAdmin(),
                user.getCreatedAt(),
                user.getUser_image()
        );
    }
}