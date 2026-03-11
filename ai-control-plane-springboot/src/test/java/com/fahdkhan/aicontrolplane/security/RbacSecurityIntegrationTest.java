package com.fahdkhan.aicontrolplane.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetailsService;

class RbacSecurityIntegrationTest {

    @Test
    void shouldProvideAdminAndUserInMemoryAccounts() {
        SecurityConfig config = new SecurityConfig();
        UserDetailsService service = config.userDetailsService(config.passwordEncoder());

        assertEquals("admin", service.loadUserByUsername("admin").getUsername());
        assertEquals("user", service.loadUserByUsername("user").getUsername());
    }
}
