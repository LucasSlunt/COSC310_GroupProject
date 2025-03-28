package com.example.task_manager.controller_tests;

import java.awt.PageAttributes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TaskRequestDTO;
import com.example.task_manager.service.TeamMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.task_manager.enums.TaskPriority;

import java.time.LocalDate;
import java.util.Arrays;

import com.example.task_manager.controller.TaskController;

@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
public class TaskControllerTest {

     @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamMemberService teamMemberService;

    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Test Create Task
     */
    @Test
    void testCreateTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int teamId = uniqueId + 1;
        TaskDTO mockTask = new TaskDTO(uniqueId, "Task Title " + uniqueId, "Description", false, "Open", LocalDate.now(), null, teamId, null,  TaskPriority.LOW);

        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Task Title " + uniqueId,
                "Description",
                false,
                "Open",
                LocalDate.of(2025, 3, 11),
                Arrays.asList(1, 2, 3),
                teamId,
                TaskPriority.LOW
        );

        when(teamMemberService.createTask(any(TaskRequestDTO.class))).thenReturn(mockTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task Title " + uniqueId))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.isLocked").value(false))
                .andExpect(jsonPath("$.status").value("Open"))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.priority").value("LOW"));
    }

    /**
     * Test Delete Task
     */
    @Test
    void testDeleteTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        doNothing().when(teamMemberService).deleteTask(uniqueId);

        mockMvc.perform(delete("/api/tasks/" + uniqueId))
                .andExpect(status().isNoContent());
    }

    /**
     * Test Edit Task
     */
    @Test
    void testEditTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int teamId = uniqueId + 1;

        TaskDTO requestDTO = new TaskDTO(
                uniqueId,
                "Updated Title " + uniqueId,
                "Updated Description",
                false,
                "In Progress",
                LocalDate.now().plusDays(3),
                null,
                teamId,
                null,
                TaskPriority.HIGH
        );

        when(teamMemberService.editTask(eq(uniqueId), any(TaskDTO.class))).thenReturn(requestDTO);

        mockMvc.perform(put("/api/tasks/" + uniqueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title " + uniqueId))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.isLocked").value(false))
                .andExpect(jsonPath("$.status").value("In Progress"))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }
}
