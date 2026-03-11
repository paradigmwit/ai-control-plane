package com.fahdkhan.aicontrolplane.api.admin;

import com.fahdkhan.aicontrolplane.security.User;
import com.fahdkhan.aicontrolplane.user.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserProfileService userProfileService;

    public AdminUserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public List<User> listUsers() {
        return userProfileService.listSeededUsers();
    }
}
