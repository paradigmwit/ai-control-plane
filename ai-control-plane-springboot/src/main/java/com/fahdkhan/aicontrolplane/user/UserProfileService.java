package com.fahdkhan.aicontrolplane.user;

import com.fahdkhan.aicontrolplane.security.Role;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    public List<UserProfile> listSeededUsers() {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        return List.of(
                new UserProfile("u-admin", "Admin User", "admin@control-plane.local", Role.ADMIN, true, createdAt),
                new UserProfile("u-user", "Standard User", "user@control-plane.local", Role.USER, true, createdAt));
    }
}
