package com.fahdkhan.aicontrolplane.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectMapper objectMapper,
            DatabaseUserDetailsService userDetailsService) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2/**", "/api/**"))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/html/**",
                                "/css/**",
                                "/js/**",
                                "/api/health",
                                "/api/auth/**")
                        .permitAll()
                        .requestMatchers("/h2/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger/user", "/swagger/admin").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((request, response, authentication) ->
                                writeSessionResponse(response, authentication, objectMapper))
                        .failureHandler((request, response, exception) ->
                                writeErrorResponse(
                                        response,
                                        HttpStatus.UNAUTHORIZED.value(),
                                        exception instanceof BadCredentialsException
                                                ? "Invalid username or password."
                                                : exception.getMessage(),
                                        objectMapper))
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .deleteCookies("JSESSIONID", "AI_CONTROL_PLANE_REMEMBER_ME")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            objectMapper.writeValue(response.getWriter(), new AuthSessionResponse(false, null, List.of()));
                        }))
                .rememberMe(remember -> remember
                        .rememberMeParameter("rememberMe")
                        .rememberMeCookieName("AI_CONTROL_PLANE_REMEMBER_ME")
                        .tokenValiditySeconds(1209600)
                        .key("ai-control-plane-remember-me")
                        .userDetailsService(userDetailsService))
                .sessionManagement(session -> session
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> writeErrorResponse(
                                response,
                                HttpStatus.UNAUTHORIZED.value(),
                                "Authentication required.",
                                objectMapper))
                        .accessDeniedHandler((request, response, exception) -> writeErrorResponse(
                                response,
                                HttpStatus.FORBIDDEN.value(),
                                "Access denied.",
                                objectMapper)))
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private void writeSessionResponse(HttpServletResponse response, Authentication authentication, ObjectMapper objectMapper)
            throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), AuthSessionResponse.from(authentication));
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message, ObjectMapper objectMapper)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(message));
    }

    private record ErrorResponse(String message) {
    }
}
