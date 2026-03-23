package com.fahdkhan.aicontrolplane.security;

import java.util.List;
import org.springframework.security.core.Authentication;

public record AuthSessionResponse(boolean authenticated, String username, List<String> roles) {

    public static AuthSessionResponse from(Authentication authentication) {
        return new AuthSessionResponse(
                true,
                authentication.getName(),
                authentication.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .sorted()
                        .toList());
    }
}
