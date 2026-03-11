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
        return _ -> {
            if (userRepository.findByEmailIgnoreCase("admin").isEmpty()) {
                userRepository.save(User.builder()
                        .name("admin")
                        .email("admin")
                        .passwordHash(passwordEncoder.encode("admin"))
                        .role(Role.ROLE_ADMIN)
                        .enabled(true)
                        .build());
            }

            if (userRepository.findByEmailIgnoreCase("user").isEmpty()) {
                userRepository.save(User.builder()
                        .name("user")
                        .email("user")
                        .passwordHash(passwordEncoder.encode("user"))
                        .role(Role.ROLE_USER)
                        .enabled(true)
                        .build());
            }
        };
    }
}
