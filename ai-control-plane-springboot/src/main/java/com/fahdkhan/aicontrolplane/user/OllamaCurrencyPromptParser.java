package com.fahdkhan.aicontrolplane.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class OllamaCurrencyPromptParser {

    private static final String CHAT_PATH = "/api/generate";
    private static final String SYSTEM_PROMPT = """
            Extract a currency conversion request and reply with JSON only.
            Return exactly these fields: amount, source_currency, target_currency.
            amount must be numeric.
            source_currency and target_currency must be valid 3-letter ISO 4217 currency codes.
            Do not include markdown, commentary, or additional keys.
            """;
    private static final Set<String> ISO_CURRENCY_CODES = Currency.getAvailableCurrencies().stream()
            .map(Currency::getCurrencyCode)
            .collect(java.util.stream.Collectors.toUnmodifiableSet());

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final OllamaProviderProperties.Provider provider;

    @Autowired
    public OllamaCurrencyPromptParser(
            RestClient.Builder restClientBuilder,
            OllamaProviderProperties properties) {
        this(restClientBuilder.build(), new ObjectMapper(), properties.defaultOllamaProvider());
    }

    OllamaCurrencyPromptParser(
            RestClient restClient,
            ObjectMapper objectMapper,
            OllamaProviderProperties.Provider provider) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.provider = provider;
    }

    public Optional<ConversionInput> parse(String prompt) {
        if (provider == null || !StringUtils.hasText(provider.baseUrl()) || !StringUtils.hasText(provider.model())) {
            return Optional.empty();
        }

        try {
            ChatResponse response = restClient.post()
                    .uri(provider.baseUrl() + CHAT_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ChatRequest(
                            provider.model(),
                            SYSTEM_PROMPT,
                            prompt == null ? "" : prompt,
                            false
                        ))
                    .retrieve()
                    .body(ChatResponse.class);
            return parseResponse(response);
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    private Optional<ConversionInput> parseResponse(ChatResponse response) {
        if (response == null || response.message() == null || !StringUtils.hasText(response.message().content())) {
            return Optional.empty();
        }

        try {
            JsonNode root = objectMapper.readTree(response.message().content());
            JsonNode amountNode = root.get("amount");
            JsonNode sourceNode = root.get("source_currency");
            JsonNode targetNode = root.get("target_currency");
            if (amountNode == null || sourceNode == null || targetNode == null
                    || !amountNode.isNumber() || !sourceNode.isTextual() || !targetNode.isTextual()) {
                return Optional.empty();
            }

            BigDecimal amount = amountNode.decimalValue();
            if (amount.signum() <= 0) {
                return Optional.empty();
            }

            String sourceCurrency = normalizeCurrencyCode(sourceNode.asText());
            String targetCurrency = normalizeCurrencyCode(targetNode.asText());
            if (sourceCurrency == null || targetCurrency == null) {
                return Optional.empty();
            }

            return Optional.of(new ConversionInput(amount, sourceCurrency, targetCurrency));
        } catch (JsonProcessingException ex) {
            return Optional.empty();
        }
    }

    private String normalizeCurrencyCode(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() != 3 || !ISO_CURRENCY_CODES.contains(normalized)) {
            return null;
        }
        return normalized;
    }

    record ChatRequest(String model, String system, String prompt, boolean stream) {
    }

    record Message(String role, String content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ChatResponse(MessageContent message) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record MessageContent(@JsonAlias("content") String content) {
    }
}
