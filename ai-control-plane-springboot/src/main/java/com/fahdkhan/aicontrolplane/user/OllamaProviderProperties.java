package com.fahdkhan.aicontrolplane.user;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "llm")
public record OllamaProviderProperties(List<Provider> providers) {

    public Provider defaultOllamaProvider() {
        return providers == null ? null : providers.stream()
                .filter(provider -> "ollama".equalsIgnoreCase(provider.type()))
                .filter(provider -> Boolean.TRUE.equals(provider.defaultProvider()))
                .findFirst()
                .orElseGet(() -> providers.stream()
                        .filter(provider -> "ollama".equalsIgnoreCase(provider.type()))
                        .findFirst()
                        .orElse(null));
    }

    public record Provider(
            String id,
            String type,
            String baseUrl,
            String model,
            Boolean defaultProvider,
            Double costPer1kTokens) {
    }
}
