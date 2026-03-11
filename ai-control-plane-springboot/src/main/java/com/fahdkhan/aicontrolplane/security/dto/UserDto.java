package com.fahdkhan.aicontrolplane.security.dto;

import com.fahdkhan.aicontrolplane.security.Role;
import com.fahdkhan.aicontrolplane.security.User;
import java.time.Instant;

public record UserDto(
        Long id,
        String name,
        String email,
        Role role,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
