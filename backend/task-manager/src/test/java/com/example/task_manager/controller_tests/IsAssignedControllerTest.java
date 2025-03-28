package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.controller.IsAssignedController;
import com.example.task_manager.service.IsAssignedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IsAssignedController.class)
@ActiveProfiles("test")
public class IsAssignedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IsAssignedService isAssignedService;

    /**
     * Assign a Team Member to a Task
     */
    @Test
    void testAssignToTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int taskId = uniqueId + 101;
        int teamMemberId = uniqueId + 202;
        int isAssignedId = uniqueId + 303;

        IsAssignedDTO mockAssignment = new IsAssignedDTO(isAssignedId, taskId, teamMemberId, uniqueId);

        when(isAssignedService.assignToTask(teamMemberId, taskId)).thenReturn(mockAssignment);

        mockMvc.perform(post("/api/assignments/" + teamMemberId + "/task/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAssignedId").value(isAssignedId))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.teamMemberId").value(teamMemberId));
    }

    /**
     * Unassign a Team Member from a Task
     */
    @Test
    void testUnassignFromTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int taskId = uniqueId + 101;
        int teamMemberId = uniqueId + 202;
        int isAssignedId = uniqueId + 303;

        IsAssignedDTO mockAssignment = new IsAssignedDTO(isAssignedId, taskId, teamMemberId, uniqueId);

        when(isAssignedService.unassignFromTask(teamMemberId, taskId)).thenReturn(mockAssignment);

        mockMvc.perform(delete("/api/assignments/" + teamMemberId + "/task/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAssignedId").value(isAssignedId));
    }

    /**
     * Check if a Team Member is Assigned to a Task
     */
    @Test
    void testIsAssignedToTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int taskId = uniqueId + 101;
        int teamMemberId = uniqueId + 202;

        when(isAssignedService.isAssignedToTask(teamMemberId, taskId)).thenReturn(true);

        mockMvc.perform(get("/api/assignments/" + teamMemberId + "/task/" + taskId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
