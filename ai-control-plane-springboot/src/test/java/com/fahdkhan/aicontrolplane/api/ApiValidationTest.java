package com.fahdkhan.aicontrolplane.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fahdkhan.aicontrolplane.api.error.GlobalExceptionHandler;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ExecutionPlanController.class)
@Import(GlobalExceptionHandler.class)
class ApiValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExecutionPlanService executionPlanService;

    @Test
    void shouldReturnValidationEnvelopeWhenPlanIdMissing() throws Exception {
        when(executionPlanService.save(any())).thenReturn(new ExecutionPlanDto("p1", "{}", Instant.now()));

        mockMvc.perform(post("/api/v1/execution-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"planId\":\"\",\"metadata\":\"{}\",\"createdAt\":\"2026-01-01T00:00:00Z\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors[0].field").value("planId"));
    }
}
