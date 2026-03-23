package com.fahdkhan.aicontrolplane.api.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class AuthSessionControllerTest {

    @Test
    void shouldReturnAnonymousSessionWhenAuthenticationMissing() {
        AuthSessionController controller = new AuthSessionController();

        var response = controller.session(null);

        assertFalse(response.authenticated());
        assertEquals(List.of(), response.roles());
    }

    @Test
    void shouldReturnAuthenticatedSessionDetails() {
        AuthSessionController controller = new AuthSessionController();
        var authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        var response = controller.session(authentication);

        assertTrue(response.authenticated());
        assertEquals("admin", response.username());
        assertEquals(List.of("ROLE_ADMIN"), response.roles());
    }
}
