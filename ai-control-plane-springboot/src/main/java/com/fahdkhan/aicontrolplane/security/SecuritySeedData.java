package com.fahdkhan.aicontrolplane.security;

import com.fahdkhan.aicontrolplane.security.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecuritySeedData {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmailIgnoreCase("admin@local.dev").isEmpty()) {
                userRepository.save(User.builder()
                        .name("Local Admin")
                        .email("admin@local.dev")
                        .passwordHash(passwordEncoder.encode("Admin@123"))
                        .role(Role.ROLE_ADMIN)
                        .enabled(true)
                        .build());
            }

            if (userRepository.findByEmailIgnoreCase("user@local.dev").isEmpty()) {
                userRepository.save(User.builder()
                        .name("Local User")
                        .email("user@local.dev")
                        .passwordHash(passwordEncoder.encode("User@123"))
                        .role(Role.ROLE_USER)
                        .enabled(true)
                        .build());
            }
        };
    }
}
