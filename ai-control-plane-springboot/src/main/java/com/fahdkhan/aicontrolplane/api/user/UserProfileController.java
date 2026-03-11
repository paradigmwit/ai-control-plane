package com.fahdkhan.aicontrolplane.api.user;

import com.fahdkhan.aicontrolplane.user.UserProfile;
import com.fahdkhan.aicontrolplane.user.UserProfileService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public List<UserProfile> listUsers() {
        return userProfileService.listSeededUsers();
    }
}
