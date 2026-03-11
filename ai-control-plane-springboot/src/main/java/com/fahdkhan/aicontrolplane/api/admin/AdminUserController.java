package com.fahdkhan.aicontrolplane.api.admin;

import com.fahdkhan.aicontrolplane.user.UserProfile;
import com.fahdkhan.aicontrolplane.user.UserProfileService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserProfileService userProfileService;

    public AdminUserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public List<UserProfile> listUsers() {
        return userProfileService.listSeededUsers();
    }
}
