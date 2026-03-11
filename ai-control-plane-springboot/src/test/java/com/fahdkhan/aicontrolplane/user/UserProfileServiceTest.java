package com.fahdkhan.aicontrolplane.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fahdkhan.aicontrolplane.security.Role;
import java.util.List;

import com.fahdkhan.aicontrolplane.security.User;
import org.junit.jupiter.api.Test;

class UserProfileServiceTest {

    @Test
    void shouldReturnAdminAndUserProfiles() {
        UserProfileService service = new UserProfileService();

        List<User> users = service.listSeededUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> user.getRole() == Role.ROLE_ADMIN && user.getName().contains("admin")));
        assertTrue(users.stream().anyMatch(user -> user.getRole() == Role.ROLE_USER && user.getName().contains("user")));
    }
}
