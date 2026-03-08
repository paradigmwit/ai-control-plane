package com.fahdkhan.aicontrolplane.api;

import com.fahdkhan.aicontrolplane.api.error.GlobalExceptionHandler;
import com.fahdkhan.aicontrolplane.api.orchestration.ExecutionTimelineResponse;
import com.fahdkhan.aicontrolplane.model.ExecutionStatus;
import com.fahdkhan.aicontrolplane.orchestration.ExecutionCommandService;
import com.fahdkhan.aicontrolplane.persistence.dto.ExecutionPlanDto;
import com.fahdkhan.aicontrolplane.persistence.entity.ExecutionInstance;
import com.fahdkhan.aicontrolplane.persistence.service.ExecutionPlanService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrchestrationController.class)
@Import(GlobalExceptionHandler.class)
class OrchestrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExecutionPlanService executionPlanService;

    @MockitoBean
    private ExecutionCommandService executionCommandService;

    @Test
    void shouldCreatePlan() throws Exception {
        when(executionPlanService.save(any())).thenReturn(new ExecutionPlanDto("p1", "{}", Instant.now()));

        mockMvc.perform(post("/api/v1/planning/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"intent\":\"summarize\",\"metadata\":\"{}\",\"budgetLimit\":1.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDATED"));
    }

    @Test
    void shouldExposeTimeline() throws Exception {
        when(executionCommandService.timeline("e1"))
                .thenReturn(new ExecutionTimelineResponse("e1", "RUNNING", List.of()));

        mockMvc.perform(get("/api/v1/executions/e1/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionId").value("e1"));
    }

    @Test
    void shouldStartExecution() throws Exception {
        ExecutionInstance execution = new ExecutionInstance();
        execution.setExecutionId("e1");
        execution.setStatus(ExecutionStatus.RUNNING);
        when(executionCommandService.start("e1")).thenReturn(execution);

        mockMvc.perform(post("/api/v1/executions/e1:start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }
}
