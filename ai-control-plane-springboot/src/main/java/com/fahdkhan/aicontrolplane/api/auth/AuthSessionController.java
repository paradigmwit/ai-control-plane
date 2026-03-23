package com.fahdkhan.aicontrolplane.api.auth;

import com.fahdkhan.aicontrolplane.security.AuthSessionResponse;
import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthSessionController {

    @GetMapping("/session")
    public AuthSessionResponse session(Authentication authentication) {
        if (authentication == null
                || authentication instanceof AnonymousAuthenticationToken
                || !authentication.isAuthenticated()) {
            return new AuthSessionResponse(false, null, List.of());
        }

        return AuthSessionResponse.from(authentication);
    }
}
