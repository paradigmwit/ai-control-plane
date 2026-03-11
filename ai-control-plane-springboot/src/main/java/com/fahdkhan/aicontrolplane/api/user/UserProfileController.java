package com.fahdkhan.aicontrolplane.api.user;

import com.fahdkhan.aicontrolplane.security.User;
import com.fahdkhan.aicontrolplane.user.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public List<User> listUsers() {
        return userProfileService.listSeededUsers();
    }
}
