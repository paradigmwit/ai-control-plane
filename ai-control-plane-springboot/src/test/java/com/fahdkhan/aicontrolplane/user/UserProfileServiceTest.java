package com.fahdkhan.aicontrolplane.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fahdkhan.aicontrolplane.security.Role;
import java.util.List;
import org.junit.jupiter.api.Test;

class UserProfileServiceTest {

    @Test
    void shouldReturnAdminAndUserProfiles() {
        UserProfileService service = new UserProfileService();

        List<UserProfile> users = service.listSeededUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> user.role() == Role.ADMIN && user.email().contains("admin")));
        assertTrue(users.stream().anyMatch(user -> user.role() == Role.USER && user.email().contains("user")));
    }
}
