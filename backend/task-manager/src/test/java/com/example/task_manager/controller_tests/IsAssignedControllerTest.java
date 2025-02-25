package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.controller.IsAssignedController;
import com.example.task_manager.service.IsAssignedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IsAssignedController.class)
public class IsAssignedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IsAssignedService isAssignedService;

    @InjectMocks
    private IsAssignedController isAssignedController;

    private IsAssignedDTO mockAssignment;

    @BeforeEach
    void setUp() {
        mockAssignment = new IsAssignedDTO(1, 101, 202, 303);
    }

    // Assign a Team Member to a Task
    @Test
    void testAssignToTask() throws Exception {
        when(isAssignedService.assignToTask(202, 101)).thenReturn(mockAssignment);

        mockMvc.perform(post("/api/assignments/202/task/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAssignedId").value(1))
                .andExpect(jsonPath("$.taskId").value(101))
                .andExpect(jsonPath("$.teamMemberId").value(202));
    }

    // Unassign Team Member from Task
    @Test
    void testUnassignFromTask() throws Exception {
        when(isAssignedService.unassignFromTask(202, 101)).thenReturn(mockAssignment);

        mockMvc.perform(delete("/api/assignments/202/task/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAssignedId").value(1));
    }

    // Check if Assigned
    @Test
    void testIsAssignedToTask() throws Exception {
        when(isAssignedService.isAssignedToTask(202, 101)).thenReturn(true);

        mockMvc.perform(get("/api/assignments/202/task/101"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
