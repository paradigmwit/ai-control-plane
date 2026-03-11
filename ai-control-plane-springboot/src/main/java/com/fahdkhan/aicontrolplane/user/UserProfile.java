package com.fahdkhan.aicontrolplane.user;

import com.fahdkhan.aicontrolplane.security.Role;
import java.time.Instant;

public record UserProfile(
        String id,
        String name,
        String email,
        Role role,
        boolean enabled,
        Instant createdAt) {
}
