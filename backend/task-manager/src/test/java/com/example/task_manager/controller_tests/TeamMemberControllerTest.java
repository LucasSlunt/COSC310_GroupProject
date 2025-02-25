package com.example.task_manager.controller_tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.controller.TeamMemberController;
import com.example.task_manager.service.TeamMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

@WebMvcTest(TeamMemberController.class)
public class TeamMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamMemberService teamMemberService;

    @InjectMocks
    private TeamMemberController teamMemberController;

    private TaskDTO mockTask;

    @BeforeEach
    void setUp() {
        mockTask = new TaskDTO(1, "Task Title", "Description", false, "Open", LocalDate.now(), 1);
    }

    // Create a Task
    @Test
    void testCreateTask() throws Exception {
        when(teamMemberService.createTask(anyString(), anyString(), anyBoolean(), anyString(),
                any(LocalDate.class), any(LocalDate.class), any(), any()))
                .thenReturn(mockTask);

        mockMvc.perform(post("/api/tasks")
                .param("title", "Task Title")
                .param("description", "Description")
                .param("isLocked", "false")
                .param("status", "Open")
                .param("teamId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task Title"));
    }

    // Delete Task
    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(teamMemberService).deleteTask(1);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    // Edit Task
    @Test
    void testEditTask() throws Exception {
        TaskDTO mockTask = new TaskDTO(1, "Updated Title", "Updated Description", false, "In Progress", LocalDate.now().plusDays(3), 1);

        when(teamMemberService.editTask(anyInt(), anyString(), anyString(), anyBoolean(),
                anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockTask);

        mockMvc.perform(put("/api/tasks/1")
                .param("title", "Updated Title")
                .param("description", "Updated Description")
                .param("status", "In Progress"));
    }

    // Assign Member to Task
    @Test
    void testAssignToTask() throws Exception {
        IsAssignedDTO assignedDTO = new IsAssignedDTO(1, 1, 1, 1);
        when(teamMemberService.assignToTask(1, 1)).thenReturn(assignedDTO);

        mockMvc.perform(post("/api/tasks/1/assign/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1));
    }

    // Change Password (Placeholder)
    @Test
    void testChangePassword() throws Exception {
        
    }
}
