package com.fahdkhan.aicontrolplane.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .name("basicAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }

    @Bean
    public GroupedOpenApi userApiGroup() {
        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch("/api/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApiGroup() {
        return GroupedOpenApi.builder()
                .group("admin-api")
                .pathsToMatch("/api/admin/**")
                .build();
    }
}
