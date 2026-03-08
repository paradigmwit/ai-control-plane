package com.fahdkhan.aicontrolplane.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeCoreResourceEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/execution-plans")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/execution-plans/p1/steps")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/execution-plans/p1/step-dependencies")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/execution-instances")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/executions/e1/steps")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/llm-metadata")).andExpect(status().isOk());
    }
}
